import { Checkbox, Heading, Text } from '@chakra-ui/react'
import { useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { MapContent } from '../../common-components/map/MapContent'
import { MapDataItemView, MapMarkerShape } from '../../util/views/map.view'

export default function MapPage() {
  const [showUserLocation, setShowUserLocation] = useState(false)
  const mapData: MapDataItemView[] = [
    {
      displayName: 'Lorem ipsum dolor sit',
      location: {
        latitude: 47.47303,
        longitude: 19.0531,
        accuracy: 0
      },
      description: undefined,
      marker: {
        color: 'brand.600',
        shape: MapMarkerShape.SQUARE
      }
    }
  ]
  return (
    <CmschPage>
      <Helmet>Térkép</Helmet>
      <Heading>Térkép</Heading>
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját pozícióm mutatása
      </Checkbox>
      <MapContent mapData={mapData} showUserLocation={showUserLocation} />
      <Text>Csak annak a helyzete látható, akinél a helymegosztás engedélyezve (használatban) van.</Text>
      <Text>A saját pozíciódat csak te látod, nem kerül megosztásra mással.</Text>
    </CmschPage>
  )
}
