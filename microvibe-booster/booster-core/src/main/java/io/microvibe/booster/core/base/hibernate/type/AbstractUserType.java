package io.microvibe.booster.core.base.hibernate.type;

import org.apache.commons.lang3.SerializationUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.util.compare.EqualsHelper;
import org.hibernate.usertype.ParameterizedType;
import org.hibernate.usertype.UserType;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

@SuppressWarnings("rawtypes")
public abstract class AbstractUserType implements UserType, ParameterizedType, Serializable {

	private static final long serialVersionUID = 1L;
	protected Properties parameters;

	public Properties getParameters() {
		return parameters;
	}

	@Override
	public void setParameterValues(Properties parameters) {
		this.parameters = parameters;
	}

	@Override
	public abstract int[] sqlTypes();

	@Override
	public abstract Class returnedClass();

	/**
	 * 从JDBC ResultSet读取数据,将其转换为自定义类型后返回 ,
	 * names中包含了当前自定义类型的映射字段名称,
	 * 此方法要求对null值进行处理
	 * <br>
	 * {@inheritDoc}
	 */
	@Override
	public abstract Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
		throws HibernateException, SQLException;

	/**
	 * 本方法将在Hibernate进行数据保存时被调用,
	 * 通过PreparedStateme将自定义数据写入到对应的数据库表字段
	 * <br>
	 * {@inheritDoc}
	 */
	@Override
	public abstract void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
		throws HibernateException, SQLException;

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return EqualsHelper.areEqual(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x == null ? 0 : x.hashCode();
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null || !isMutable() || !(value instanceof Serializable)) {
			return value;
		}
		return SerializationUtils.clone((Serializable) value);
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		if (original == null || !isMutable() || !(original instanceof Serializable)) {
			return original;
		}
		return SerializationUtils.clone((Serializable) original);
	}

}
