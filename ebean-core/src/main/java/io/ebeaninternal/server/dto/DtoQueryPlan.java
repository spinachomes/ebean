package io.ebeaninternal.server.dto;

import io.ebean.core.type.DataReader;
import io.ebean.meta.MetricVisitor;

import java.sql.SQLException;

/**
 * Knows how to read and map rows into a Bean.
 */
public interface DtoQueryPlan {

  /**
   * Read the row data and return the DTO bean.
   */
  Object readRow(DataReader dataReader) throws SQLException;

  /**
   * Add an event to the query execution statistics.
   */
  void collect(long exeMicros);

  /**
   * Visit the metric (if not empty).
   */
  void visit(MetricVisitor visitor);
}
