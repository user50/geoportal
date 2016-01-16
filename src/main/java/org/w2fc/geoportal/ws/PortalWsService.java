package org.w2fc.geoportal.ws;

import org.w2fc.geoportal.ws.model.*;


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

	public Long createPoint(RequestPoint rp);

	Long createLine(RequestLine rp);

	Long createPolygon(RequestPolygon requestLine);

	public void delete(Long layerId, Long id);
}
