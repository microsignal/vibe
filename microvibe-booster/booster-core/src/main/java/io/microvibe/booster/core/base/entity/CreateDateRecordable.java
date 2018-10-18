package io.microvibe.booster.core.base.entity;

import java.sql.Timestamp;
import java.util.Date;

public interface CreateDateRecordable {

	Date getCreateDate();

	void setCreateDate(Date date);
}
