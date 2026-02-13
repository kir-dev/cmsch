import { useState } from 'react'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useTinderCommunity } from '../../api/hooks/community/useTinderCommunity.ts'
import { useTinderInteractionSend } from '../../api/hooks/community/useTinderInteractionSend.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { type TinderCommunity } from '../../util/views/tinder.ts'

const TinderPage = () => {
  const config = useConfigContext()?.components
  const component = config?.communities

  const { data, isLoading, isError } = useTinderCommunity()
  const interact = useTinderInteractionSend()

  const [interactionCount, setInteractionCount] = useState<number>(0)
  const [currentCommunity, setCurrentCommunity] = useState<TinderCommunity | null>(null)

  if (!component || !component.tinderEnabled) return <ComponentUnavailable />

  if (isError || isLoading || !data) return <PageStatus isLoading={isLoading} isError={isError} />

  return (
    <CmschPage loginRequired>
      <title>{config?.app?.siteName || 'CMSch'} | Tinder</title>
    </CmschPage>
  )
}

export default TinderPage
