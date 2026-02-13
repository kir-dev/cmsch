import { useState } from 'react'
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

  // swipe state to animate the current top card out
  const [swipe, setSwipe] = useState<{ id: number; dir: 'left' | 'right' } | null>(null)
  // keep track of optimistically removed cards by id
  const [removedIds, setRemovedIds] = useState<Set<number>>(new Set())

  if (!component || !component.tinderEnabled) return <ComponentUnavailable />

  if (isError || isLoading || !communities) return <PageStatus isLoading={isLoading} isError={isError} />

  // Filter communities that are NOT_SEEN
  const unseen = communities.filter((c) => c.status === 'NOT_SEEN')
  const displayed = unseen.filter((c) => !removedIds.has(c.id))

  // container for stacking cards
  const stackContainerStyle: React.CSSProperties = {
    width: 320,
    height: 460,
    position: 'relative',
    margin: '2rem auto'
  }

  const cardWrapperStyle = (index: number, total: number, cardId: number): React.CSSProperties => {
    const isTop = index === 0
    const isSwiping = swipe?.id === cardId

    // base absolute overlap
    const base: React.CSSProperties = {
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
    }

    // interactive only for the top card
    if (!isTop) base.pointerEvents = 'none'

    // if this card is being swiped, apply an animation transform
    if (isSwiping && swipe) {
      const dir = swipe.dir === 'right' ? 1 : -1
      return {
        ...base,
        transition: 'transform 320ms ease, opacity 320ms ease',
        transform: `translateX(${dir * 480}px) rotate(${dir * 18}deg)`,
        opacity: 0
      }
    }

    // no swipe
    return {
      ...base,
      transition: 'transform 320ms ease, opacity 320ms ease'
    }
  }

  const removeCardAfterAnimation = (id: number) => {
    // wait for animation to finish then mark as removed
    window.setTimeout(() => {
      setRemovedIds((prev) => {
        const copy = new Set(prev)
        copy.add(id)
        return copy
      })
      setSwipe(null)
    }, 340)
  }

  const handleLike = (c: TinderCommunity) => {
    // prevent double swipes
    if (swipe) return
    // start swipe animation to the right
    setSwipe({ id: c.id, dir: 'right' })
    // optimistic removal after animation
    removeCardAfterAnimation(c.id)
    // send interaction (fire-and-forget optimistic)
    interact.mutate({ communityId: c.id, liked: true })
  }

  const handleDislike = (c: TinderCommunity) => {
    if (swipe) return
    setSwipe({ id: c.id, dir: 'left' })
    removeCardAfterAnimation(c.id)
    interact.mutate({ communityId: c.id, liked: false })
  }

  return (
    <CmschPage loginRequired>
      <title>{config?.app?.siteName || 'CMSch'} | Tinder</title>

      <div style={{ padding: '2rem' }}>
        {displayed.length === 0 ? (
          <div>No new communities to show.</div>
        ) : (
          <div style={stackContainerStyle} aria-live="polite">
            {displayed.map((c, idx) => (
              <div key={c.id} style={cardWrapperStyle(idx, displayed.length, c.id)}>
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
