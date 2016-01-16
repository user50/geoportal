package org.w2fc.geoportal.widget;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.w2fc.geoportal.domain.GeoLayer;
import org.w2fc.geoportal.layer.GeoLayerUIAdapter;
import org.w2fc.geoportal.utils.ServiceRegistry;

@Controller
@RequestMapping(value = "/widget")
public class WidgetRest {

	@Autowired
    private ServiceRegistry serviceRegistry;
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getWidget(
			@RequestParam("z") String zoom,
			@RequestParam("lat") String lat,
			@RequestParam("lon") String lon,
			@RequestParam("version") Integer version,
			@RequestParam(value="layers", required=false) String layers,
			@RequestParam(value="origin") String url,
			Model model, HttpServletRequest request,
			HttpServletResponse response) {
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=\"widget.html\"";
		response.setHeader(headerKey, headerValue);
		model.addAttribute("lat", lat);
		model.addAttribute("lon", lon);
		model.addAttribute("zoom", zoom);
		if(layers != null){
			String[] layerIds = layers.split(",");
			List<GeoLayerUIAdapter> l = new ArrayList<GeoLayerUIAdapter>(layerIds.length);
			for (int i = 0; i < layerIds.length; i++) {
				Long layerId = Long.valueOf(layerIds[i]);
				GeoLayer layer = serviceRegistry.getLayerDao().get(layerId);
				l.add(new GeoLayerUIAdapter(layer));
			}
			model.addAttribute("layers", l);
		}
		model.addAttribute("href", url);
		//model.addAttribute("href","http://geoportal.yarcloud.ru/");
		model.addAttribute("ver", version);
		return "markup/widget";
	}

}
