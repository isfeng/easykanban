package controllers;

import org.apache.commons.lang.math.NumberUtils;

import models.Kanban;
import models.KanbanRoleHolder;
import models.deadbolt.RoleHolder;
import play.mvc.Controller;
import securesocial.provider.SocialUser;
import controllers.deadbolt.DeadboltHandler;
import controllers.deadbolt.ExternalizedRestrictionsAccessor;
import controllers.deadbolt.RestrictedResourcesHandler;
import controllers.securesocial.SecureSocial;

public class KanbanAccessHandler extends Controller implements DeadboltHandler
{
	
	// private static final RestrictedResourcesHandler RESTRICTED_RESOURCES_HANDLER = new KanbanResourcesHandler();
	
	
	@Override
	public void beforeRoleCheck()
	{
		try
		{
			long id = NumberUtils.toLong(params.get("id"));
			Kanban k = Kanban.findById(id);
			SecureSocial.DeadboltHelper.beforeRoleCheck();
		}
		catch (Throwable throwable)
		{
			throwable.printStackTrace();
			throw new RuntimeException(throwable);
		}
	}
	
	
	@Override
	public RoleHolder getRoleHolder()
	{
		SocialUser suser = SecureSocial.getCurrentUser();
		return KanbanRoleHolder.getBySocialUser(suser);
	}
	
	
	@Override
	public void onAccessFailure(String controllerClassName)
	{
		forbidden();
	}
	
	
	@Override
	public ExternalizedRestrictionsAccessor getExternalizedRestrictionsAccessor()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	
	@Override
	public RestrictedResourcesHandler getRestrictedResourcesHandler()
	{
		// return RESTRICTED_RESOURCES_HANDLER;
		return null;
	}
	
}
