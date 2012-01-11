package com.enonic.cms.web.common.json;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;

import java.text.SimpleDateFormat;

@Component
@Provider
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public final class JsonProvider
    extends JacksonJsonProvider
{
    public JsonProvider()
    {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat( new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );
        mapper.getSerializationConfig().disable( SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS );
        mapper.getSerializationConfig().setSerializationInclusion( JsonSerialize.Inclusion.NON_NULL );
        setMapper( mapper );
    }
}
