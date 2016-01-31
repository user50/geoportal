package org.w2fc.geoportal.ws;

import org.w2fc.geoportal.ws.model.*;

import java.util.List;


public interface PortalWsService {

    public GetObjectsResponse getObjects(Long descriptor);
    
    public GetLayersResponse getLayers();

	public Long getObjectsCount(Long layerId);

	public GetObjectsResponse getObjectsPartial(Long layerId, Long start,
			Long count);

	public Boolean getHasChanged(Long layerId, Long revId);

	public GetObjectsResponse getObjectsChanged(Long layerId, Long revId);

	public GetObjectsResponse getPin(Double lat, Double lon);

	public Long getLastRevision(Long layerId);
    
	public GeoObjectFullAdapter getObject(Long id);

	public void createObjects(List<RequestGeoObject> geoObjectsReq);

	public void updateObjects(List<RequestGeoObject> geoObjectsReq);

	public Long createPoint(RequestPoint rp);

	Long createLine(RequestLine rp);

	Long createPolygon(RequestPolygon requestLine);

	void updatePoint(Long id, RequestPoint requestPoint);

	void updateLine(Long id, RequestLine requestLine);

	void updatePolygon(Long id, RequestPolygon requestPolygon);

	public void delete(Long layerId, Long id);
}
