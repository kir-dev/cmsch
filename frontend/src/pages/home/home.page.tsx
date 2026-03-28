import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { ComponentUnavailable } from '@/common-components/ComponentUnavailable'

import { CmschPage } from '@/common-components/layout/CmschPage'
import Markdown from '@/common-components/Markdown'
import { useTime } from '@/hooks/useDate.ts'
import Clock from '../countdown/components/clock'
import { EmbeddedVideo } from './components/EmbeddedVideo'
import HomePageEventList from './components/HomePageEventList.tsx'
import HomePageGalleryCarousel from './components/HomePageGalleryCarousel.tsx'
import HomePageNewsList from './components/HomePageNewsList.tsx'

const HomePage = () => {
  const config = useConfigContext()
  const countdownConfig = config?.components?.countdown
  const homeConfig = config?.components?.home

  const now = useTime()
  const timeToCountTo = countdownConfig?.timeToCountTo ? countdownConfig?.timeToCountTo * 1000 : now

  if (!homeConfig) return <ComponentUnavailable />
  const videoIds = homeConfig?.youtubeVideoIds
    ?.split(',')
    ?.map((videoId) => videoId.trim())
    ?.filter(Boolean)

  return (
    <CmschPage>
      {homeConfig?.welcomeMessage && (
        <h1 className="text-4xl md:text-5xl font-bold font-heading text-center mt-10 leading-tight">
          {homeConfig?.welcomeMessage.split('{}')[0] + ' '}
          {homeConfig?.welcomeMessage.split('{}').length > 1 && (
            <>
              <span className="text-primary">{config?.components?.app?.siteName || 'CMSch'}</span>{' '}
              {homeConfig?.welcomeMessage.split('{}')[1]}
            </>
          )}
        </h1>
      )}
      {countdownConfig?.enabled && countdownConfig.showRemainingTime && (
        <>
          <h2 className="text-2xl font-bold text-center mt-5">{countdownConfig?.topMessage}</h2>
          <Clock countTo={timeToCountTo} />
        </>
      )}
      {homeConfig.showNews && config?.components?.news && <HomePageNewsList />}

      {videoIds?.length > 0 && (
        <div className="flex flex-col gap-4 mt-10">
          {videoIds.map((videoId) => (
            <EmbeddedVideo key={videoId} id={videoId} />
          ))}
        </div>
      )}

      {homeConfig?.content && (
        <div className="mt-10">
          <Markdown text={homeConfig?.content} />
        </div>
      )}

      {homeConfig?.showEvents && config?.components?.event && <HomePageEventList />}
      {homeConfig.showGalleryImages && config?.components?.gallery && <HomePageGalleryCarousel />}
    </CmschPage>
  )
}

export default HomePage
