import { Checkbox, Heading, Text } from '@chakra-ui/react'
import { useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useLocationQuery } from '../../api/hooks/location/useLocationQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { MapContent } from '../../common-components/map/MapContent'

export default function MapPage() {
  const [showUserLocation, setShowUserLocation] = useState(false)
  const locationQuery = useLocationQuery()

  return (
    <CmschPage>
      <Helmet>Térkép</Helmet>
      <Heading>Térkép</Heading>
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját pozícióm mutatása
      </Checkbox>
      <MapContent mapData={locationQuery.data ?? []} showUserLocation={showUserLocation} />
      <Text>Csak annak a helyzete látható, akinél a helymegosztás engedélyezve (használatban) van.</Text>
      <Text>A saját pozíciódat csak te látod, nem kerül megosztásra mással.</Text>
    </CmschPage>
  )
}
