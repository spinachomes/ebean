package io.ebeaninternal.server.expression.platform;

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
}
