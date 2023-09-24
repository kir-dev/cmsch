import { Box, Checkbox, Divider, Heading, Text } from '@chakra-ui/react'

import { useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLocationQuery } from '../../api/hooks/location/useLocationQuery'
import { l } from '../../util/language'
import { MapContent } from './MapContent'

function GroupMapContainer() {
  const profileConfig = useConfigContext()?.components.profile
  const [showUserLocation, setShowUserLocation] = useState(false)
  const locationQuery = useLocationQuery()

  return (
    <Box>
      <Divider my={10} borderWidth={2} />
      {profileConfig && <Heading my={5}>{profileConfig.groupLeadersHeader} pozíciója</Heading>}
      <Checkbox my={3} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        {l('location-show-own')}
      </Checkbox>
      <MapContent mapData={locationQuery.data ?? []} showUserLocation={showUserLocation} />
      <Text>{l('location-description')}</Text>
      <Text>{l('location-privacy')}</Text>
    </Box>
  )
}

export default GroupMapContainer
