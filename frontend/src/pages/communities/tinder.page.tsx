import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useTinderCommunity } from '../../api/hooks/community/useTinderCommunity.ts'
import { useTinderInteractionSend } from '../../api/hooks/community/useTinderInteractionSend.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { type TinderCommunity } from '../../util/views/tinder.ts'
import { TinderCard } from './components/TinderCard'

const TinderPage = () => {
  const config = useConfigContext()?.components
  const component = config?.communities

  const { data: communities, isLoading, isError } = useTinderCommunity()
  const interact = useTinderInteractionSend()

  if (!component || !component.tinderEnabled) return <ComponentUnavailable />

  if (isError || isLoading || !communities) return <PageStatus isLoading={isLoading} isError={isError} />

  // Filter communities that are NOT_SEEN
  const unseen = communities.filter((c) => c.status === 'NOT_SEEN')

  // container for stacking cards
  const stackContainerStyle: React.CSSProperties = {
    width: 320,
    height: 460,
    position: 'relative',
    margin: '2rem auto'
  }

  const cardWrapperStyle = (index: number, total: number): React.CSSProperties => ({
    position: 'absolute',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    // ensure top card has highest z-index so it is visible
    zIndex: total - index,
    // fully overlap so only top card appears visible
    transform: 'translateY(0)'
  })

  const handleLike = async (c: TinderCommunity) => {
    try {
      await interact.mutateAsync({ communityId: c.id, liked: true })
    } catch (e) {
      // ignore for now; PageStatus would display server errors elsewhere
      console.error('like failed', e)
    }
  }

  const handleDislike = async (c: TinderCommunity) => {
    try {
      await interact.mutateAsync({ communityId: c.id, liked: false })
    } catch (e) {
      console.error('dislike failed', e)
    }
  }

  return (
    <CmschPage loginRequired>
      <title>{config?.app?.siteName || 'CMSch'} | Tinder</title>

      <div style={{ padding: '2rem' }}>
        {unseen.length === 0 ? (
          <div>No new communities to show.</div>
        ) : (
          <div style={stackContainerStyle} aria-live="polite">
            {unseen.map((c, idx) => (
              <div key={c.id} style={cardWrapperStyle(idx, unseen.length)}>
                <TinderCard data={c} depth={idx} onLike={handleLike} onDislike={handleDislike} />
              </div>
            ))}
          </div>
        )}
      </div>
    </CmschPage>
  )
}

export default TinderPage
