package io.ebeaninternal.server.expression.platform;

import io.ebeaninternal.server.expression.Op;

/**
 * Postgres JSON and ARRAY expression handler
 */
final class Dm8DbExpression extends BasicDbExpression {

  @Override
  public void arrayContains(DbExpressionRequest request, String propName, boolean contains, Object... values) {
    if (!contains) {
      request.append("not (");
    }
    for (int i = 0; i < values.length; i++) {
      request.append("json_contains(replace(").property(propName).append(",'\"',''),?)");
      if (i < values.length - 1) {
        request.append(" and ");
      }
    }
    if (!contains) {
      request.append(')');
    }
  }

  @Override
  public void json(DbExpressionRequest request, String propName, String path, Op operator, Object value) {
    String[] paths = path.split("\\.");
    request.property(propName);
    for (String _path : paths) {
      request.append(" ->> '").append(_path).append("'");
    }
    request.append(operator.bind());
  }

  @Override
  public void arrayIsEmpty(DbExpressionRequest request, String propName, boolean empty) {
    if (empty) {
      request.append("not (");
    }
    request.append("json_contains_path(replace(" + propName + ",'\"',''),'ONE','$[0]')");
    if (!empty) {
      request.append(')');
    }
  }
}
