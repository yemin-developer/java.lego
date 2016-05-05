package com.lia.common.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

public enum DeleteHandler {
   INSTANCE;
   private String _driver = "com.mysql.jdbc.Driver";

   public void delete(Configure p, CommonObject item) throws Exception{
      Connection connection = null;
      try {
         DbUtils.loadDriver(_driver);
         connection = DriverManager.getConnection(p.getPassword(), p.getUser(), p.getPassword());
         QueryRunner query = new QueryRunner();
         String script = buildDeleteScript(item.getObjectName(), item.exportKeyFieldMap());
         query.update(script);
      }
      catch (Exception ex){
         throw ex;
      }
      finally {
         connection.close();
      }
   }
   
   public void empty(Configure c, CommonObject item) throws Exception {
      Connection connection = null;
      try {
         DbUtils.loadDriver(_driver);
         connection = DriverManager.getConnection(c.getPassword(), c.getUser(), c.getPassword());
         QueryRunner query = new QueryRunner();
         String script = buildDeleteScript(item.getObjectName());
         query.update(script);
      }
      catch (Exception ex){
         throw ex;
      }
      finally {
         connection.close();
      }
   }
   
   private String buildDeleteScript(String entityName, Map<String, String> keyFieldMap) throws Exception {
      String script = String.format("delete from %s ", entityName);
      String condition = "";
      for (Entry<String, String> entry : keyFieldMap.entrySet()) {
         String fieldName = entry.getKey();
         String fieldValue = entry.getValue();
         if (condition.length() > 0){
            condition += " and ";
         }
         condition += String.format("%s = %s", fieldName, fieldValue);         
      }
      if (condition.length() > 0){
         condition = " where " + condition;
      }
      script += condition;
      return script;
   }
   
   private String buildDeleteScript(String entityName) throws Exception {
      String script = String.format("delete from %s ", entityName);
      return script;
   }
}
