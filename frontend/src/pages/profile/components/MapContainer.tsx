import { Box, Checkbox, useToast } from '@chakra-ui/react'
import Map from './openlayers/Map'
import { Layers, TileLayer } from './openlayers/Layers/'
import { Controls, FullScreenControl } from './openlayers/Controls'
import Stamen from 'ol/source/Stamen'
import Feature from 'ol/Feature'
import Point from 'ol/geom/Point'
import { fromLonLat } from 'ol/proj'
import Icon from 'ol/style/Icon'
import { Style } from 'ol/style'
import { VectorLayer } from './openlayers/Layers'
import VectorSource from 'ol/source/Vector'
import { useEffect, useState } from 'react'
import { useGeolocated } from 'react-geolocated'
import { GroupMemberLocationView } from '../../../util/views/groupMemberLocation.view'
import { useLocationQuery } from '../../../api/hooks/useLocationQuery'

const addMarkers = (data: GroupMemberLocationView[]) => {
  const iconStyle = new Style({
    image: new Icon({
      anchor: [0.5, 1],
      src: 'img/marker.png',
      scale: 0.05
    })
  })
  const features = data.map((item) => {
    const feature = new Feature({
      geometry: new Point(fromLonLat([item.logitude, item.latitude]))
    })
    feature.setStyle(iconStyle)
    feature.set('name', item.name)
    feature.set('timestamp', item.timestamp)
    return feature
  })
  return features
}

type MapContainerProps = {
  groupName: string
}

export const MapContainer = ({ groupName }: MapContainerProps) => {
  const [showUserLocation, setShowUserLocation] = useState<boolean>(false)
  const [watchStarted, setWatchStarted] = useState<boolean>(false)
  const toast = useToast()
  const locationQuery = useLocationQuery(groupName, () =>
    toast({
      title: 'A pozíciók nem érhetőek el.',
      status: 'error'
    })
  )

  const { coords, isGeolocationAvailable, isGeolocationEnabled, timestamp, getPosition } = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 5000,
    suppressLocationOnMount: true,
    watchPosition: true
  })

  useEffect(() => {
    if (showUserLocation && !watchStarted) {
      getPosition()
      setWatchStarted(true)
    }
  }, [showUserLocation, watchStarted])

  useEffect(() => {
    if (showUserLocation && (!isGeolocationAvailable || !isGeolocationEnabled)) {
      toast({ title: 'Helymeghatározás nem elérhető', status: 'error' })
    }
  }, [showUserLocation, isGeolocationAvailable, isGeolocationEnabled])

  return (
    <Box>
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját helyzetem mutatása
      </Checkbox>
      <Map>
        <Layers>
          <TileLayer source={new Stamen({ layer: 'terrain' })} />
          {locationQuery.isSuccess && <VectorLayer source={new VectorSource({ features: addMarkers(locationQuery.data) })} zIndex={1} />}
          {showUserLocation && coords && (
            <VectorLayer
              source={
                new VectorSource({
                  features: addMarkers([
                    {
                      name: 'A te pozíciód',
                      logitude: coords.longitude,
                      latitude: coords.latitude,
                      accuracy: coords.accuracy,
                      timestamp: timestamp || 0
                    }
                  ])
                })
              }
              zIndex={1}
            />
          )}
        </Layers>
        <Controls>
          <FullScreenControl />
        </Controls>
      </Map>
    </Box>
  )
}
