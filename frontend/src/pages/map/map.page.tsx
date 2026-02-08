import { Box, Checkbox, Heading, Text } from '@chakra-ui/react'
import { useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useLocationQuery } from '../../api/hooks/location/useLocationQuery'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { MapContent } from '../../common-components/map/MapContent'
import Markdown from '../../common-components/Markdown'
import { l } from '../../util/language'

export default function MapPage() {
  const [showUserLocation, setShowUserLocation] = useState(false)
  const locationQuery = useLocationQuery()
  const config = useConfigContext()?.components
  const component = config?.location
  const app = config?.app

  return (
    <CmschPage>
      <title>
        {app?.siteName || 'CMSch'} | {component?.title || 'Térkép'}
      </title>
      <Heading as="h1" variant="main-title">
        Térkép
      </Heading>
      {component?.topMessage && <Markdown text={component.topMessage} />}
      <Checkbox my={3} checked={showUserLocation} onChange={(e) => setShowUserLocation(e.target.checked)}>
        {l('location-show-own')}
      </Checkbox>
      <MapContent mapData={locationQuery.data ?? []} showUserLocation={showUserLocation} />
      <Text>{l('location-description')}</Text>
      <Text>{l('location-privacy')}</Text>
      {component?.bottomMessage && (
        <Box pt={4}>
          <Markdown text={component.bottomMessage} />
        </Box>
      )}
    </CmschPage>
  )
}
