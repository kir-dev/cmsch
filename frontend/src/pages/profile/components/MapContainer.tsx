import { Box, Checkbox, Heading, Text, useToast } from '@chakra-ui/react'
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
import { useConfigContext } from '../../../api/contexts/config/ConfigContext'

const addMarkers = (data: GroupMemberLocationView[]) => {
  const stroke = new Stroke({ color: 'white', width: 4 })
  const regularIconStyle = new Style({
    image: new Circle({
      fill: new Fill({ color: '#ef7564' }),
      radius: 10,
      stroke
    })
  })
  const userIconStyle = new Style({
    image: new Circle({
      fill: new Fill({ color: '#172b4d' }),
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

export const MapContainer = () => {
  const [showUserLocation, setShowUserLocation] = useState<boolean>(false)
  const [watchStarted, setWatchStarted] = useState<boolean>(false)
  const toast = useToast()
  const profileConfig = useConfigContext()?.components.profile

  const { coords, isGeolocationAvailable, isGeolocationEnabled, timestamp, getPosition } = useGeolocated({
    positionOptions: {
      enableHighAccuracy: false
    },
    userDecisionTimeout: 10000,
    suppressLocationOnMount: true
  })

  const locationQuery = useLocationQuery(
    () =>
      toast({
        title: 'A pozíciók nem érhetőek el.',
        status: 'error'
      }),
    () => {
      if (watchStarted) {
        getPosition()
      }
    }
  )

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
      {profileConfig && <Heading my={5}>{profileConfig.groupLeadersHeader} pozicíója</Heading>}
      <Checkbox ml={1} checked={showUserLocation} disabled={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját pozícióm mutatása
      </Checkbox>
      <Map>
        <Layers>
          <TileLayer source={new Stamen({ layer: 'terrain' })} />
          {locationQuery.isSuccess && <VectorLayer source={new VectorSource({ features: addMarkers(locationQuery.data) })} zIndex={2} />}
          {showUserLocation && coords && (
            <VectorLayer
              source={
                new VectorSource({
                  features: addMarkers([
                    {
                      id: -1,
                      alias: 'A te pozíciód',
                      userName: '(GPS szenzor alapján)',
                      longitude: coords.longitude,
                      latitude: coords.latitude,
                      accuracy: coords.accuracy,
                      timestamp: ((timestamp || 0) - new Date().getTimezoneOffset() * 60000) / 1000
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
      <Text>Csak akkor jelennek meg a pizíciók, ha a helymegosztás használatban van.</Text>
    </Box>
  )
}
