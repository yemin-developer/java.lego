package com.lia.common.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;

public enum CreateHandler {
   INSTANCE;
   private String _driver = "com.mysql.jdbc.Driver";
   
   public void create(Configure p, CommonObject item) throws Exception{
      Connection connection = null;
      try {
         DbUtils.loadDriver(_driver);
         connection = DriverManager.getConnection(p.getPassword(), p.getUser(), p.getPassword());
         QueryRunner query = new QueryRunner();
         String script = buildCreateScript(item.getObjectName(), item.exportFieldMap());
         query.update(script);
      }
      catch (Exception ex){
         throw ex;
      }
      finally {
         connection.close();
      }
   }
   
   public void create(Configure c, List<CommonObject> objectList) throws Exception {
      Connection connection = null;
      CommonObject obj = null;
      if (objectList.size() > 0){
         obj = objectList.get(0);
      }
      try {
         if (obj != null) {
            DbUtils.loadDriver(_driver);
            connection = DriverManager.getConnection(c.getPassword(), c.getUser(), c.getPassword());
            QueryRunner query = new QueryRunner();
         
            query.batch(buildBatchCreateScript(obj.getObjectName(), obj.getFieldName()), buildBatchCreateValue(obj.getObjectName(), objectList));
         }
      }
      catch (Exception ex){
         throw ex;
      }
      finally {
         connection.close();
      }
   }
   
   private String buildCreateScript(String entityName, Map<String, String> fieldMap) throws Exception {
      String fieldScript = "";
      String valueScript = "";
      for (Entry<String, String> entry : fieldMap.entrySet()) {
         String fieldName = entry.getKey();
         String fieldValue = entry.getValue();
         if (fieldScript.length() > 0){
            fieldScript += ", ";
         }
         if (valueScript.length() > 0){
            valueScript += ", ";
         }
         fieldScript += fieldName;
         valueScript += fieldValue;   
      }
      
      String script = String.format("insert into %s(%s) values(%s)", entityName, fieldScript, valueScript);
      return script;
   }
   
   private String buildBatchCreateScript(String entityName, List<String> fieldNameList) throws Exception {
      String fieldScript = "";
      String valueScript = "";
      for (String fieldName : fieldNameList) {
         if (fieldScript.length() > 0){
            fieldScript += ", ";
         }
         if (valueScript.length() > 0){
            valueScript += ", ";
         }
         fieldScript += fieldName;
         valueScript += "?";   
      }
      
      String script = String.format("insert into %s(%s) values(%s)", entityName, fieldScript, valueScript);
      return script;
   }
   
   private Object[][] buildBatchCreateValue(String entityName, List<CommonObject> objectList) throws Exception {
      Object[][] obj = new Object[objectList.size()][];
      int index = 0;
      for (CommonObject commonObject : objectList){
         obj[index] = commonObject.getObject();
         index ++;
      }
      return obj;
   }

}