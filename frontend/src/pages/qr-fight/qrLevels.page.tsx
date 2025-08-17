import { Flex, Heading, TabList, TabPanel, TabPanels, Tabs, useBreakpoint, useBreakpointValue } from '@chakra-ui/react'
import { Helmet } from 'react-helmet-async'
import { FaQrcode } from 'react-icons/fa'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { useQrLevelsQuery } from '../../api/hooks/qr/useQrLevelsQuery'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'
import { CustomTabButton } from '../../common-components/CustomTabButton'
import { CmschPage } from '../../common-components/layout/CmschPage'
import { LinkButton } from '../../common-components/LinkButton'
import Markdown from '../../common-components/Markdown'
import { PageStatus } from '../../common-components/PageStatus'
import { AbsolutePaths } from '../../util/paths'
import { DataDisplayWrapper } from './components/DataDisplayWrapper'

export default function QrLevelsPage() {
  const component = useConfigContext()?.components.qrFight
  const { data, isLoading, isError } = useQrLevelsQuery()
  const tabsSize = useBreakpointValue({ base: 'sm', md: 'md' })
  const breakpoint = useBreakpoint()

  if (!component || !component.enabled) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} title={component.title} />

  return (
    <CmschPage>
      <Helmet>{component.title}</Helmet>
      <Flex align="baseline" justifyContent="space-between" wrap="wrap">
        <Heading as="h1" variant="main-title" mt={5}>
          {component.title}
        </Heading>
        <LinkButton my={5} colorScheme="brand" leftIcon={<FaQrcode />} href={`${AbsolutePaths.TOKEN}/scan`}>
          QR kód beolvasása
        </LinkButton>
      </Flex>
      <Markdown text={component.topMessage} />
      <Tabs mt={10} size={tabsSize} isFitted={breakpoint !== 'base'} variant="soft-rounded" colorScheme="brand">
        <TabList>
          <CustomTabButton>Fő szintek</CustomTabButton>
          <CustomTabButton>Extra szintek</CustomTabButton>
        </TabList>
        <TabPanels>
          <TabPanel px={0}>
            {data.mainLevels.map((a) => (
              <DataDisplayWrapper level={a} key={a.name} />
            ))}
          </TabPanel>
          <TabPanel px={0}>
            {data.extraLevels.map((a) => (
              <DataDisplayWrapper level={a} key={a.name} />
            ))}
          </TabPanel>
        </TabPanels>
      </Tabs>
    </CmschPage>
  )
}
