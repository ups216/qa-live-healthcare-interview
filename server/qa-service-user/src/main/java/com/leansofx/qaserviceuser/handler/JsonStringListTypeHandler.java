package com.leansofx.qaserviceuser.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MyBatis TypeHandler：MySQL JSON 列 与 {@code List<String>} 的双向映射。
 *
 * <p>用途：t_doctor.specialties 使用 JSON 列存储擅长领域。
 * {@code map-underscore-to-camel-case} 只解决列名到属性的映射，
 * <b>不会</b>做 JSON 反序列化，因此必须在 resultMap 中显式声明本处理器。
 *
 * <p>选型理由：医生列表 QPS 极低，用 JSON 列比拆子表少一次 JOIN / N+1 处理，
 * 改动面最小。若将来换成 VARCHAR 逗号分隔，只需替换本类实现，上层 Mapper 无需修改。
 */
@MappedTypes(List.class)
public class JsonStringListTypeHandler extends BaseTypeHandler<List<String>> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<String>> TYPE_REF = new TypeReference<List<String>>() {
    };

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setString(i, OBJECT_MAPPER.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("序列化 List<String> 为 JSON 失败: " + parameter, e);
        }
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parse(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parse(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parse(cs.getString(columnIndex));
    }

    private List<String> parse(String raw) {
        if (raw == null || raw.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return OBJECT_MAPPER.readValue(raw, TYPE_REF);
        } catch (Exception e) {
            throw new IllegalArgumentException("解析 JSON 列到 List<String> 失败，原始值: " + raw, e);
        }
    }
}
