import { Box, Checkbox, Divider, Heading, Text, useToast } from '@chakra-ui/react'

import { useMemo, useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLocationQuery } from '../../api/hooks/location/useLocationQuery'
import { l } from '../../util/language'
import { MapDataItemView, MapMarkerShape } from '../../util/views/map.view'
import { MapContent } from './MapContent'

function GroupMapContainer() {
  const toast = useToast()

  const profileConfig = useConfigContext()?.components.profile
  const [showUserLocation, setShowUserLocation] = useState(false)
  const locationQuery = useLocationQuery(() =>
    toast({
      title: l('location-query-failed'),
      status: 'error'
    })
  )
  const mapData = useMemo<MapDataItemView[]>(() => {
    if (!locationQuery.data) return []
    return locationQuery.data.map((location) => ({
      displayName: location.userName ?? '',
      location: {
        latitude: location.latitude,
        longitude: location.longitude,
        accuracy: location.accuracy
      },
      description: undefined,
      marker: {
        color: 'brand.600',
        shape: MapMarkerShape.CIRCLE
      }
    }))
  }, [locationQuery.data])
  return (
    <Box>
      <Divider my={10} borderWidth={2} />
      {profileConfig && <Heading my={5}>{profileConfig.groupLeadersHeader} pozicíója</Heading>}
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját pozícióm mutatása
      </Checkbox>
      <MapContent mapData={mapData} showUserLocation={showUserLocation} />
      <Text>Csak annak a helyzete látható, akinél a helymegosztás engedélyezve (használatban) van.</Text>
      <Text>A saját pozíciódat csak te látod, nem kerül megosztásra mással.</Text>
    </Box>
  )
}

export default GroupMapContainer
