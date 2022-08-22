import { useRef, useState, useEffect } from 'react'
import MapContext from './MapContext'
import * as ol from 'ol'
import './Map.css'
import { HasChildren } from '../../../../util/react-types.util'
import { fromLonLat } from 'ol/proj'
import { Box } from '@chakra-ui/react'

const Map = ({ children }: HasChildren) => {
  const mapRef = useRef<HTMLDivElement>(null)
  const [map, setMap] = useState<ol.Map | null>(null)

  useEffect(() => {
    let options = {
      view: new ol.View({ zoom: 16, center: fromLonLat([19.056084, 47.4752744]), maxZoom: 20 }),
      layers: [],
      controls: [],
      overlays: []
    }
    let mapObject = new ol.Map(options)
    mapObject.setTarget(mapRef.current || undefined)
    setMap(mapObject)

    return () => mapObject.setTarget(undefined)
  }, [])

  map?.on('click', (e) => {
    map.forEachFeatureAtPixel(e.pixel, (feature, layer) => {
      console.log(feature.get('alias'))
    })
  })

  return (
    <MapContext.Provider value={map}>
      <Box ref={mapRef} className="ol-map">
        {children}
      </Box>
    </MapContext.Provider>
  )
}
export default Map
