import { Checkbox, Heading, Text } from '@chakra-ui/react'
import { useState } from 'react'
import { Helmet } from 'react-helmet-async'
import { useLocationQuery } from '../../api/hooks/location/useLocationQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { MapContent } from '../../common-components/map/MapContent'
import { l } from '../../util/language'

export default function MapPage() {
  const [showUserLocation, setShowUserLocation] = useState(false)
  const locationQuery = useLocationQuery()

  return (
    <CmschPage>
      <Helmet>Térkép</Helmet>
      <Heading>Térkép</Heading>
      <Checkbox my={3} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        {l('location-show-own')}
      </Checkbox>
      <MapContent mapData={locationQuery.data ?? []} showUserLocation={showUserLocation} />
      <Text>{l('location-description')}</Text>
      <Text>{l('location-privacy')}</Text>
    </CmschPage>
  )
}
