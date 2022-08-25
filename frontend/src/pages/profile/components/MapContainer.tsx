import { Box, Checkbox, Heading, useToast } from '@chakra-ui/react'
import Map from './openlayers/Map'
import { Layers, TileLayer } from './openlayers/Layers/'
import FullScreenControl from './openlayers/FullScreenControl'
import Stamen from 'ol/source/Stamen'
import Feature from 'ol/Feature'
import Point from 'ol/geom/Point'
import { fromLonLat } from 'ol/proj'
import { Circle, Fill, Stroke, Style } from 'ol/style'
import { VectorLayer } from './openlayers/Layers'
import VectorSource from 'ol/source/Vector'
import { useEffect, useState } from 'react'
import { useGeolocated } from 'react-geolocated'
import { GroupMemberLocationView } from '../../../util/views/groupMemberLocation.view'
import { useLocationQuery } from '../../../api/hooks/useLocationQuery'
import { config } from 'process'
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'

const addMarkers = (data: GroupMemberLocationView[]) => {
  const stroke = new Stroke({ color: 'white', width: 2 })
  const regularIconStyle = new Style({
    image: new Circle({
      fill: new Fill({ color: 'blue' }),
      radius: 10,
      stroke
    })
  })
  const userIconStyle = new Style({
    image: new Circle({
      fill: new Fill({ color: 'orange' }),
      radius: 10,
      stroke
    })
  })
  const features = data.map((item) => {
    const feature = new Feature({
      geometry: new Point(fromLonLat([item.longitude, item.latitude]))
    })
    feature.setStyle(item.id === -1 ? userIconStyle : regularIconStyle)
    feature.set('person', item)
    feature.set('timestamp', item.timestamp)
    return feature
  })
  return features
}

const mockdata: GroupMemberLocationView[] = [
  {
    id: 2,
    alias: 'Szenyor Csávó',
    userName: 'szenorusz maximus',
    longitude: 19.056084,
    latitude: 47.4752744,
    accuracy: 3,
    timestamp: 1661452901
  }
]

export const MapContainer = () => {
  const [showUserLocation, setShowUserLocation] = useState<boolean>(false)
  const [watchStarted, setWatchStarted] = useState<boolean>(false)
  const toast = useToast()
  const profileConfig = useConfigContext()?.components.profile

  const locationQuery = useLocationQuery(() =>
    toast({
      title: 'A pozíciók nem érhetőek el.',
      status: 'error'
    })
  )

  const { coords, isGeolocationAvailable, isGeolocationEnabled, timestamp, getPosition } = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 10000,
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
      {profileConfig && <Heading>{profileConfig.groupLeadersHeader} pozicíója</Heading>}
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját helyzetem mutatása
      </Checkbox>
      <Map>
        <Layers>
          <TileLayer source={new Stamen({ layer: 'terrain' })} />
          {locationQuery.isSuccess && <VectorLayer source={new VectorSource({ features: addMarkers(mockdata) })} zIndex={2} />}
          {showUserLocation && coords && (
            <VectorLayer
              source={
                new VectorSource({
                  features: addMarkers([
                    {
                      id: -1,
                      alias: 'A te pozíciód',
                      userName: 'Fekete Sámuel',
                      longitude: coords.longitude,
                      latitude: coords.latitude,
                      accuracy: coords.accuracy,
                      timestamp: (timestamp || 0) / 1000
                    }
                  ])
                })
              }
              zIndex={1}
            />
          )}
        </Layers>

        <FullScreenControl />
      </Map>
    </Box>
  )
}
