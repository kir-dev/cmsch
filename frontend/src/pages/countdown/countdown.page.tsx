import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { Title } from '@/util/TitleProvider.tsx'
import { type PropsWithChildren, useMemo } from 'react'
import Clock from './components/clock'
import { parseTopMessage } from './countdown.util'

const CountdownPage = ({ children }: PropsWithChildren) => {
  const component = useConfigContext()?.components?.countdown
  const countTo = useMemo(() => {
    try {
      if (!component) return new Date()
      return new Date(component?.timeToCountTo * 1000)
    } catch (e) {
      console.error(e)
      return new Date()
    }
  }, [component])
  // eslint-disable-next-line react-hooks/purity
  if (component?.enabled && component?.showOnly && (component?.keepOnAfterCountdownOver || countTo.getTime() > Date.now())) {
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
              {component.showRemainingTime && <Clock countTo={countTo} />}
            </div>
          </div>
        </div>
      </div>
    )
  } else return <>{children}</>
}

export default CountdownPage
