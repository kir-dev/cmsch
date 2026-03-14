import { useConfigContext } from '@/api/contexts/config/ConfigContext'
import { useServiceContext } from '@/api/contexts/service/ServiceContext'
import { ErrorBoundary } from '@/util/errorBoundary.tsx'
import { l } from '@/util/language'
import { AbsolutePaths } from '@/util/paths'
import { type PropsWithChildren, useEffect } from 'react'
import { Navigate } from 'react-router'
import { EnableNotifications } from '../EnableNotifications'
import { Footer } from '../footer/Footer'
import { Navbar } from '../navigation/Navbar'
import { Warning } from '../Warning'
import { ScrollToTop } from './ScrollToTop'

export const CmschLayout = ({ children }: PropsWithChildren) => {
  const config = useConfigContext()
  const { sendMessage } = useServiceContext()
  const component = config?.components?.app

  useEffect(() => {
    if (!component) sendMessage(l('component-unavailable'))
  }, [component, sendMessage])

  if (!component) {
    return <Navigate to={AbsolutePaths.ERROR} />
  }

  return (
    <div className="flex flex-col min-h-screen">
      <ScrollToTop />
      <Navbar />
      <main className="flex-1 pb-20">
        <ErrorBoundary>
          <Warning />
          <EnableNotifications />
          <ErrorBoundary>{children}</ErrorBoundary>
        </ErrorBoundary>
      </main>
      <Footer />
    </div>
  )
}
