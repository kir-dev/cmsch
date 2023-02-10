import { PropsWithChildren, useEffect, useRef, useState } from 'react'
import MapContext from './MapContext'
import * as ol from 'ol'
import './Map.css'
import { fromLonLat } from 'ol/proj'
import OLOVerlay from 'ol/Overlay'
import { Box } from '@chakra-ui/react'
import { GroupMemberLocationView } from '../../../../util/views/groupMemberLocation.view'
import { Popup } from '../Popup'
import { EventsKey } from 'ol/events'
import { unByKey } from 'ol/Observable'

const overlay = new OLOVerlay({ id: 'ov' })

const Map = ({ children }: PropsWithChildren) => {
  const mapRef = useRef<HTMLDivElement>(null)
  const popupRef = useRef<HTMLDivElement>(null)
  const [map, setMap] = useState<ol.Map | null>(null)
  const [selectedPerson, setSelectedPerson] = useState<GroupMemberLocationView | undefined>(undefined)
  const [listenerKey, setListenerKey] = useState<EventsKey | undefined>()

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

  useEffect(() => {
    if (listenerKey) unByKey(listenerKey)
    setListenerKey(
      map?.on('click', (e) => {
        onMapClicked(e)
      })
    )
  }, [map, selectedPerson])

  const onMapClicked = (e: ol.MapBrowserEvent<any>) => {
    let featureCount = 0
    map?.forEachFeatureAtPixel(e.pixel, (feature) => {
      featureCount++
      const person = feature.get('person')
      const coords = fromLonLat([person.longitude, person.latitude])
      if (map?.getOverlays().getLength() === 0) {
        overlay.setPosition(coords)
        overlay.setElement(popupRef.current || undefined)
        map?.addOverlay(overlay)
      } else {
        if (person.id !== selectedPerson?.id) {
          map?.getOverlayById('ov').setPosition(coords)
        }
      }
      setSelectedPerson(person)
    })
    if (featureCount === 0) {
      setSelectedPerson(undefined)
    }
  }

  return (
    <MapContext.Provider value={map}>
      <Box ref={mapRef} className="ol-map">
        {children}
        <Popup ref={popupRef} person={selectedPerson} onClose={() => setSelectedPerson(undefined)} />
      </Box>
    </MapContext.Provider>
  )
}

export default Map
