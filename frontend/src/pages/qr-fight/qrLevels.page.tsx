import { CmschPage } from '../../common-components/layout/CmschPage'
import { Helmet } from 'react-helmet-async'
import { Flex, Heading, TabList, TabPanel, TabPanels, Tabs, useBreakpoint, useBreakpointValue } from '@chakra-ui/react'
import { DataDisplayWrapper } from './components/DataDisplayWrapper'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { Navigate } from 'react-router-dom'
import Markdown from '../../common-components/Markdown'
import { useQrLevelsQuery } from '../../api/hooks/useQrLevelsQuery'
import { Loading } from '../../common-components/Loading'
import { FaQrcode } from 'react-icons/fa'
import { AbsolutePaths } from '../../util/paths'
import { LinkButton } from '../../common-components/LinkButton'
import { CustomTab } from '../events/components/CustomTab'

export default function QrLevelsPage() {
  const component = useConfigContext()?.components.qrFight
  const { data, isLoading } = useQrLevelsQuery()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()
  if (!component || !component.enabled) return <Navigate to="/" replace />
  if (isLoading) return <Loading />
  if (!data) return <Navigate to="/" />
  return (
    <CmschPage>
      <Helmet>{component.title}</Helmet>
      <Flex align="center" justifyContent="space-between" wrap="wrap">
        <Heading mt={5}>{component.title}</Heading>
        <LinkButton my={5} colorScheme="brand" leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          QR kód beolvasása
        </LinkButton>
      </Flex>
      <Markdown text={component.topMessage} />
      <Tabs mt={10} size={tabsSize} isFitted={breakpoint !== 'base'} variant="unstyled">
        <TabList>
          <CustomTab>Fő szintek</CustomTab>
          <CustomTab>Extra szintek</CustomTab>
        </TabList>
        <TabPanels>
          <TabPanel>
            {data.mainLevels.map((a) => (
              <DataDisplayWrapper level={a} key={a.name} />
            ))}
          </TabPanel>
          <TabPanel>
            {data.extraLevels.map((a) => (
              <DataDisplayWrapper level={a} key={a.name} />
            ))}
          </TabPanel>
        </TabPanels>
      </Tabs>
    </CmschPage>
  )
}
