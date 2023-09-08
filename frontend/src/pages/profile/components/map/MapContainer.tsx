import { Box, Checkbox, Divider, Heading, Text } from '@chakra-ui/react'

import { useState } from 'react'
import { useConfigContext } from '../../../../api/contexts/config/ConfigContext'
import { MapContent } from './MapContent'

function MapContainer() {
  const profileConfig = useConfigContext()?.components.profile
  const [showUserLocation, setShowUserLocation] = useState(false)
  return (
    <Box>
      <Divider my={10} borderWidth={2} />
      {profileConfig && <Heading my={5}>{profileConfig.groupLeadersHeader} pozicíója</Heading>}
      <Checkbox ml={1} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        Saját pozícióm mutatása
      </Checkbox>
      <MapContent showUserLocation={showUserLocation} />
      <Text>Csak annak a helyzete látható, akinél a helymegosztás engedélyezve (használatban) van.</Text>
      <Text>A saját pozíciódat csak te látod, nem kerül megosztásra mással.</Text>
    </Box>
  )
}

export default MapContainer
