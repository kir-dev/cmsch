import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import type { Countdown } from '@/api/contexts/config/types.ts'
import { useTime } from '@/hooks/useDate.ts'
import { Title } from '@/util/TitleProvider.tsx'
import { type PropsWithChildren } from 'react'
import Clock from './components/clock'
import { parseTopMessage } from './countdown.util'

const isCountdownActive = (component: Countdown, now: number) => {
  const countdownTo = (component?.timeToCountTo ?? 0) * 1000
  return countdownTo > now || component?.keepOnAfterCountdownOver
}

const ForcedCountdown = ({ children, component }: PropsWithChildren & { component: Countdown }) => {
  const now = useTime(1000)
  if (isCountdownActive(component, now)) {
    return (
      <div className="h-full w-full">
        <Title text={component?.title} />
        <div
          className="absolute h-full w-full top-0 left-0 z-0 bg-center bg-cover"
          style={{
            backgroundImage: `url(${component.imageUrl})`,
            filter: component.blurredImage ? 'blur(15px)' : undefined
          }}
        />
        <div className="flex flex-col h-full w-full z-10 overflow-auto relative">
          <div className="flex items-center justify-center min-h-screen">
            <div className="flex flex-col items-center w-full max-h-full">
              <h1 className="text-4xl font-bold text-center">{parseTopMessage(component.topMessage)}</h1>
              {component.showRemainingTime && <Clock countTo={component.timeToCountTo * 1000} />}
            </div>
          </div>
        </div>
      </div>
    )
  }

  return <>{children}</>
}

export const CountdownPage = ({ children }: PropsWithChildren) => {
  const component = useConfigContext()?.components?.countdown
  const timeOfLoading = useTime(undefined)

  if (component?.enabled && component?.showOnly && isCountdownActive(component, timeOfLoading)) {
    return <ForcedCountdown component={component}>{children}</ForcedCountdown>
  } else return <>{children}</>
}
