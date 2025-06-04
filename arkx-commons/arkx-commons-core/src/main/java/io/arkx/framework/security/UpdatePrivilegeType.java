package io.arkx.framework.security;

public class UpdatePrivilegeType
{
//  public static void main(String[] args)
//  {
//    HashMap<String, String> m = new HashMap();
//    m.put("com.arkxos.framework.cms.ImagePlayer", "ImagePlayerManage");
//    m.put("com.arkxos.framework.cms.ImagePlayer.Edit", "ImagePlayerManage.Edit");
//    m.put("com.arkxos.framework.cms.ImagePlayer.Delete", "ImagePlayerManage.Delete");
//    m.put("com.arkxos.framework.cms.ImagePlayer.CommitAudit", "ImagePlayerManage.CommitAudit");
//    m.put("com.arkxos.framework.cms.ImagePlayer.Audit", "ImagePlayerManage.Audit");
//    m.put("com.arkxos.framework.cms.ImagePlayer.Publish", "ImagePlayerManage.Publish");
//    
//    DAOSet<ZDPrivilege> set = new ZDPrivilege().query();
//    Privilege p = new Privilege();
//    for (ZDPrivilege priv : set)
//    {
//      p.parse(priv.getPrivs());
//      for (String key : m.keySet())
//      {
//        Integer t = (Integer)p.types.get(key);
//        if (t != null)
//        {
//          p.types.put((String)m.get(key), t);
//          p.types.remove(key);
//        }
//        Integer k = (Integer)p.keys.get(key);
//        if (k != null)
//        {
//          p.types.put((String)m.get(key), k);
//          p.types.remove(key);
//        }
//      }
//      priv.setPrivs(p.toString());
//      priv.update();
//    }
//  }
}
