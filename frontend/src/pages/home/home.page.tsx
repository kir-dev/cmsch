import { Box, Heading } from '@chakra-ui/react'
import { useMemo } from 'react'
import { Helmet } from 'react-helmet-async'
import { useConfigContext } from '../../api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable'

import { CmschPage } from '../../common-components/layout/CmschPage'
import Markdown from '../../common-components/Markdown'
import { useBrandColor } from '../../util/core-functions.util.ts'
import Clock from '../countdown/components/clock'
import { EmbeddedVideo } from './components/EmbeddedVideo'
import HomePageEventList from './components/HomePageEventList.tsx'
import HomePageGalleryCarousel from './components/HomePageGalleryCarousel.tsx'
import HomePageNewsList from './components/HomePageNewsList.tsx'

const HomePage = () => {
  const config = useConfigContext()
  const countdownConfig = config?.components?.countdown
  const homeConfig = config?.components?.home
  const brandColor = useBrandColor(500, 500)

  const countTo = useMemo(() => {
    try {
      if (!countdownConfig) return new Date()
      return new Date(countdownConfig?.timeToCountTo * 1000)
    } catch (e) {
      console.error(e)
      return new Date()
    }
  }, [countdownConfig])

  if (!homeConfig) return <ComponentUnavailable />
  const videoIds = homeConfig?.youtubeVideoIds
    ?.split(',')
    ?.map((videoId) => videoId.trim())
    ?.filter(Boolean)

  return (
    <CmschPage>
      <Helmet />
      {homeConfig?.welcomeMessage && (
        <Heading variant="main-title" as="h1" size="3xl" textAlign="center" marginTop={10} lineHeight="1.2">
          {homeConfig?.welcomeMessage.split('{}')[0] + ' '}
          {homeConfig?.welcomeMessage.split('{}').length > 1 && (
            <>
              <Heading as="span" color={brandColor} size="3xl">
                {config?.components?.app?.siteName || 'CMSch'}
              </Heading>{' '}
              {homeConfig?.welcomeMessage.split('{}')[1]}
            </>
          )}
        </Heading>
      )}
      {countdownConfig?.enabled && countdownConfig.showRemainingTime && (
        <>
          <Heading textAlign="center">{countdownConfig?.topMessage}</Heading>
          <Clock countTo={countTo} />
        </>
      )}
      {homeConfig.showNews && config?.components?.news && <HomePageNewsList />}

      {videoIds?.length > 0 && (
        <>
          {videoIds.map((videoId) => (
            <EmbeddedVideo key={videoId} id={videoId} />
          ))}
        </>
      )}

      {homeConfig?.content && (
        <Box mt={10}>
          <Markdown text={homeConfig?.content} />
        </Box>
      )}

      {homeConfig?.showEvents && config?.components?.event && <HomePageEventList />}
      {homeConfig.showGalleryImages && config?.components?.gallery && <HomePageGalleryCarousel />}
    </CmschPage>
  )
}

export default HomePage
