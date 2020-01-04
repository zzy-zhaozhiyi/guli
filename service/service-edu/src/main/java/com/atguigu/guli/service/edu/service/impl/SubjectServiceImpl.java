package com.atguigu.guli.service.edu.service.impl;

import com.atguigu.guli.common.base.util.ExcelImportUtil;
import com.atguigu.guli.service.edu.entity.Subject;
import com.atguigu.guli.service.edu.entity.vo.SubjectVo;
import com.atguigu.guli.service.edu.mapper.SubjectMapper;
import com.atguigu.guli.service.edu.service.SubjectService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 课程科目 服务实现类
 * </p>
 *
 * @author zzy
 * @since 2019-12-25
 */
@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements SubjectService {
    @Transactional//因为有多条插入数据所以进行事务的管理
    @Override
    public void batchImport(InputStream inputStream) throws Exception {
        ExcelImportUtil importUtil = new ExcelImportUtil(inputStream);

        HSSFSheet sheet = importUtil.getSheet();
        //遍历sheet就是行
        for (Row row : sheet) {
            //判断row的行数是否是0，0的话是第一行，不取
            if (row.getRowNum() == 0) {
                continue;//结束继续
            }

            Cell cell = row.getCell(0);//第一个单元格的对象（即一级对象），下面对象的值
            String cellValue = importUtil.getCellValue(cell).trim();//利用工具类获取里面的值
            if (cellValue == null || StringUtils.isEmpty(cellValue)) {
                continue;
            }
            Cell cell2 = row.getCell(1);//第二个对象，（即二级对象）
            String cellValue2 = importUtil.getCellValue(cell2).trim();

            if (cellValue2 == null || StringUtils.isEmpty(cellValue2)) {
                continue;
            }
            //插入数据库，
            //插入一级前先进行判断，是否已经存在
            Subject levelOne = this.getBytitle(cellValue);
            String parentId = null;
            if (levelOne == null) {
                Subject subject = new Subject();
                subject.setTitle(cellValue);
                this.baseMapper.insert(subject);
                parentId = subject.getId();

            } else {
                parentId = levelOne.getId();
            }

            //插入2级前进行判断
            Subject levelTwo = this.getByTitleAndParentId(cellValue2, parentId);
            Subject subject2 = null;
            if (levelTwo == null) {
                subject2 = new Subject();
                subject2.setTitle(cellValue2);
                subject2.setParentId(parentId);
                this.baseMapper.insert(subject2);
            }


        }


    }

    @Override
    public List<SubjectVo> nestedList() {
        List<SubjectVo> subjectVos = new ArrayList<>();
//根据排序和id来找出所有符合条件的
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort", "id");
        List<Subject> subjects = this.baseMapper.selectList(queryWrapper);

        ArrayList<Subject> subjectLevelOnes = new ArrayList<>();
        ArrayList<Subject> subjectLevelTwos = new ArrayList<>();
        //根据parentid来进行判断一级和二级，进行分类
        for (Subject subject : subjects) {
            if (subject.getParentId() .equalsIgnoreCase( "0")) {//这里犯了一个致命的失误，连个string比较居然写成了==应该是equals
                subjectLevelOnes.add(subject);
            } else {
                subjectLevelTwos.add(subject);
            }
        }
        //遍历一级，并进行填充subjectvos
        for (Subject subjectLevelone : subjectLevelOnes) {
            SubjectVo subjectVoOne = new SubjectVo();
            BeanUtils.copyProperties(subjectLevelone, subjectVoOne);

            //添加同一个一级目录下的，查找二级目录,也就是subjectVO的children
            ArrayList<SubjectVo> subjectVoTwoList = new ArrayList<>();
            for (Subject subjectLevelTwo : subjectLevelTwos) {
                if ((subjectLevelTwo.getParentId()).equals(subjectLevelone.getId())) {
                    SubjectVo subjectVoTwo = new SubjectVo();
                    BeanUtils.copyProperties(subjectLevelTwo, subjectVoTwo);
                    subjectVoTwoList.add(subjectVoTwo);
                }
            }
            subjectVoOne.setChildren(subjectVoTwoList);
            subjectVos.add(subjectVoOne);
        }

        return subjectVos;
    }

    //检验一级是否存在
    public Subject getBytitle(String title) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title).eq("parent_id", "0");//检验一级是否存在的条件
        Subject subject = this.baseMapper.selectOne(queryWrapper);

        return subject;
    }

    //根据条件检验2级是否已经存在
    public Subject getByTitleAndParentId(String title, String parentId) {
        QueryWrapper<Subject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("title", title).eq("parent_id", parentId);
        Subject subject = this.baseMapper.selectOne(queryWrapper);
        return subject;
    }
}
