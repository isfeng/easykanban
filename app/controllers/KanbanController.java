package controllers;

import java.util.HashMap;
import java.util.List;

import models.Kanban;
import models.StickyNote;
import models.TextNote;
import models.User;
import models.ValueStream;
import play.data.validation.Required;
import play.mvc.Controller;
import play.mvc.With;
import securesocial.provider.SocialUser;
import controllers.securesocial.SecureSocial;

@With(KanbanSecure.class)
public class KanbanController extends Controller
{
	
	public static void show(long id)
	{
		render(id);
	}
	
	
	public static void create(@Required String name, @Required String goal, String values)
	{
		if (validation.hasErrors())
		{
			params.flash(); // add http parameters to the flash scope
			validation.keep(); // keep the errors for the next request
			_new();
		}
		
		SocialUser socialUser = SecureSocial.getCurrentUser();
		User _user = User.find("byEmail", socialUser.email).first();
		
		Kanban k = new Kanban();
		k.name = name;
		k.goal = goal;
		k.user = _user;
		k.save();
		
		if (values != null && !values.equals(""))
		{
			String valuearr[] = values.split(",");
			for (String v : valuearr)
			{
				ValueStream value = new ValueStream(v, k);
				value.save();
			}
		}
		
		index();
	}
	
	
	/**
	 * cant update value stream currently
	 * @param id
	 * @param name
	 * @param goal
	 */
	public static void update(long id, String name, String goal)
	{
		SocialUser socialUser = SecureSocial.getCurrentUser();
		User _user = User.find("byEmail", socialUser.email).first();
		
		Kanban k = Kanban.findById(id);
		k.name = name;
		k.goal = goal;
		k.save();
		
		index();
	}
	
	
	public static void delete(long id)
	{
		Kanban k = Kanban.findById(id);
		TextNote.delete("kanban", k);
		ValueStream.delete("kanban=?", k);
		k.delete();
		renderJSON("OK");
	}
	
	
	public static void index()
	{
		SocialUser socialUser = SecureSocial.getCurrentUser();
		User user = User.find("byEmail", socialUser.email).first();
		List<Kanban> kanbans = Kanban.find("byUser", user).fetch();
		renderArgs.put("kanbans", kanbans);
		render();
	}
	
	
	public static void notes(long id)
	{
		List<TextNote> notes = TextNote.find("byKanban", Kanban.findById(id)).fetch();
		renderJSON(notes);
	}
	
	
	public static void getBackground(long id)
	{
		Kanban k = Kanban.findById(id);
		List<ValueStream> vs = ValueStream.find("byKanban", k).fetch();
		HashMap m = new HashMap();
		m.put("background", k.background);
		for (ValueStream valueStream : vs)
		{
			valueStream.kanban = null;
		}
		m.put("stream", vs);
		renderJSON(m);
	}
	
	
	public static void setBackground(long id, String dataURL)
	{
		Kanban k = Kanban.findById(id);
		k.background = dataURL;
		k.save();
		renderJSON("OK");
	}
	
	
	public static void _new()
	{
		render();
	}
	
	
	public static void _update(long id)
	{
		Kanban kanban = Kanban.findById(id);
		render(kanban);
	}
}