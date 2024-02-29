package com.tianlei.mybatis.service;

import com.intellij.database.psi.DbTable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class TableInfoService {
    public static TableInfoService getInstance(@NotNull Project project) {
        return project.getService(TableInfoService.class);
    }

    /**
     * 类型校验，如果存在未知类型则引导用于去条件类型
     *
     * @param dbTable 原始表对象
     * @return 是否验证通过
     */
    public boolean typeValidator(DbTable dbTable) {
        // 处理所有列
//        JBIterable<? extends DasColumn> columns = DasUtil.getColumns(dbTable);
//        List<TypeMapper> typeMapperList = CurrGroupUtils.getCurrTypeMapperGroup().getElementList();
//
//        FLAG:
//        for (DasColumn column : columns) {
//            String typeName = column.getDataType().getSpecification();
//            for (TypeMapper typeMapper : typeMapperList) {
//                // 不区分大小写查找类型
//                if (Pattern.compile(typeMapper.getColumnType(), Pattern.CASE_INSENSITIVE).matcher(typeName).matches()) {
//                    continue FLAG;
//                }
//            }
//            // 没找到类型，引导用户去添加类型
//            if (MessageDialogBuilder.yesNo(MsgValue.TITLE_INFO, String.format("数据库类型%s，没有找到映射关系，是否去添加？", typeName)).isYes()) {
//                ShowSettingsUtil.getInstance().showSettingsDialog(project, "Type Mapper");
//                return false;
//            }
//            // 用户取消添加
//            return true;
//        }
        return true;
    }
}
