import { Box, Button, Heading, Text } from '@chakra-ui/react'
import React, { useRef, useState } from 'react'
import { Link } from 'react-router'
import { useConfigContext } from '../../api/contexts/config/ConfigContext.tsx'
import { useTinderCommunity } from '../../api/hooks/community/useTinderCommunity.ts'
import { useTinderInteractionSend } from '../../api/hooks/community/useTinderInteractionSend.ts'
import { ComponentUnavailable } from '../../common-components/ComponentUnavailable.tsx'
import { CmschPage } from '../../common-components/layout/CmschPage.tsx'
import { PageStatus } from '../../common-components/PageStatus.tsx'
import { AbsolutePaths } from '../../util/paths'
import { type TinderCommunity } from '../../util/views/tinder.ts'
import { TinderCard } from './components/TinderCard'

const SWIPE_THRESHOLD = 220 // pixels required to trigger like/dislike

const TinderPage = () => {
  const config = useConfigContext()?.components
  const component = config?.communities

  const { data: communities, isLoading, isError } = useTinderCommunity()
  const interact = useTinderInteractionSend()

  // swipe state to animate the current top card out
  const [swipe, setSwipe] = useState<{ id: number; dir: 'left' | 'right' } | null>(null)
  // keep track of optimistically removed cards by id
  const [removedIds, setRemovedIds] = useState<Set<number>>(new Set())

  // current drag state (live during pointer move)
  const [drag, setDrag] = useState<{ id: number; x: number } | null>(null)
  const dragStartX = useRef<number | null>(null)
  const dragStartY = useRef<number | null>(null)
  const pendingDragId = useRef<number | null>(null)
  const activePointerId = useRef<number | null>(null)

  if (!component || !component.tinderEnabled) return <ComponentUnavailable />

  if (isError || isLoading || !communities) return <PageStatus isLoading={isLoading} isError={isError} />

  // Filter communities that are NOT_SEEN and not removed
  const unseen = communities.filter((c) => c.status === 'NOT_SEEN')
  const displayed = unseen.filter((c) => !removedIds.has(c.id))

  // container for stacking cards - responsive for mobile
  const isMobile = typeof window !== 'undefined' && window.innerWidth < 768
  const containerHeight = isMobile ? Math.min(598, window.innerHeight - 200) : 598

  const stackContainerStyle: React.CSSProperties = {
    width: '100%',
    maxWidth: 416,
    height: containerHeight,
    position: 'relative',
    margin: '1rem auto',
    padding: '0 0.5rem'
  }

  const cardWrapperBase = (index: number, total: number): React.CSSProperties => ({
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

  const doLike = (c: TinderCommunity) => {
    // start swipe animation to the right
    setSwipe({ id: c.id, dir: 'right' })
    // optimistic removal after animation
    removeCardAfterAnimation(c.id)
    // send interaction (fire-and-forget optimistic)
    interact.mutate({ communityId: c.id, liked: true })
  }

  const doDislike = (c: TinderCommunity) => {
    setSwipe({ id: c.id, dir: 'left' })
    removeCardAfterAnimation(c.id)
    interact.mutate({ communityId: c.id, liked: false })
  }

  // Pointer/touch handling improvements:
  // - don't capture pointer or prevent default on down/start so normal page scroll still works
  // - only begin a drag after movement exceeds START_THRESHOLD and is primarily horizontal
  // - when a drag is recognized, capture pointer and prevent default to keep the card interaction
  const START_THRESHOLD = 10

  const handlePointerDown = (e: React.PointerEvent, c: TinderCommunity) => {
    if (swipe) return
    let targetNode: Node | null = e.target as Node | null
    while (targetNode && targetNode.nodeType !== Node.ELEMENT_NODE) {
      targetNode = targetNode.parentNode
    }
    const el = targetNode as Element | null
    if (el && el.closest && (el.closest('button, a, [role="button"], input, textarea, select') || el.closest('[data-no-drag]'))) {
      return
    }
    // record potential drag start but don't capture or prevent default yet
    dragStartX.current = e.clientX
    dragStartY.current = e.clientY
    pendingDragId.current = c.id
    // ensure no active drag yet
    activePointerId.current = null
  }

  const handlePointerMove = (e: React.PointerEvent, c: TinderCommunity) => {
    const startX = dragStartX.current
    const startY = dragStartY.current
    if (startX == null || startY == null) return

    const dx = e.clientX - startX
    const dy = e.clientY - startY

    // if we haven't turned this into an active drag yet, decide whether to start
    if (!drag) {
      const absDx = Math.abs(dx)
      const absDy = Math.abs(dy)
      // still within jitter threshold
      if (absDx < START_THRESHOLD && absDy < START_THRESHOLD) return

      // if horizontal movement dominates, start capturing and dragging
      if (absDx > absDy && absDx >= START_THRESHOLD && pendingDragId.current === c.id) {
        try {
          e.currentTarget.setPointerCapture(e.pointerId)
        } catch {
          /* ignore capture failures */
        }
        activePointerId.current = e.pointerId
        pendingDragId.current = null
        setDrag({ id: c.id, x: dx })
        e.preventDefault()
        return
      }

      // vertical movement dominates -> treat as scroll, clear pending and don't prevent default
      if (absDy > absDx) {
        pendingDragId.current = null
        // let the browser handle scrolling
        return
      }
    }

    // if we have an active drag, only respond to the same pointer
    if (!drag || activePointerId.current !== e.pointerId) return

    setDrag({ id: c.id, x: dx })
    e.preventDefault()

    if (!swipe) {
      if (dx > SWIPE_THRESHOLD) {
        try {
          e.currentTarget.releasePointerCapture(e.pointerId)
        } catch {
          /* empty */
        }
        setDrag(null)
        dragStartX.current = null
        dragStartY.current = null
        activePointerId.current = null
        doLike(c)
        return
      }
      if (dx < -SWIPE_THRESHOLD) {
        try {
          e.currentTarget.releasePointerCapture(e.pointerId)
        } catch {
          // ignore
        }
        setDrag(null)
        dragStartX.current = null
        dragStartY.current = null
        activePointerId.current = null
        doDislike(c)
        return
      }
    }
  }

  const handlePointerUp = (e: React.PointerEvent, c: TinderCommunity) => {
    // if we never started a drag, just clear pending state
    if (!drag) {
      dragStartX.current = null
      dragStartY.current = null
      pendingDragId.current = null
      return
    }

    if (activePointerId.current !== null && activePointerId.current !== e.pointerId) return

    try {
      e.currentTarget.releasePointerCapture(e.pointerId)
    } catch {
      // ignore if not captured
    }
    const startX = dragStartX.current
    if (startX == null) {
      setDrag(null)
      dragStartX.current = null
      dragStartY.current = null
      activePointerId.current = null
      return
    }
    const dx = e.clientX - startX
    setDrag(null)
    dragStartX.current = null
    dragStartY.current = null
    activePointerId.current = null

    if (dx > SWIPE_THRESHOLD) {
      doLike(c)
      return
    }
    if (dx < -SWIPE_THRESHOLD) {
      doDislike(c)
      return
    }
  }

  const handlePointerCancel = () => {
    setDrag(null)
    dragStartX.current = null
    dragStartY.current = null
    pendingDragId.current = null
    activePointerId.current = null
  }

  // Touch event fallbacks so older mobile browsers that don't support Pointer Events still work
  const handleTouchStart = (e: React.TouchEvent, c: TinderCommunity) => {
    if (swipe) return
    // walk up to element node
    let targetNode: Node | null = e.target as Node | null
    while (targetNode && targetNode.nodeType !== Node.ELEMENT_NODE) {
      targetNode = targetNode.parentNode
    }
    const el = targetNode as Element | null
    if (el && el.closest && (el.closest('button, a, [role="button"], input, textarea, select') || el.closest('[data-no-drag]'))) {
      return
    }
    const t = e.touches && e.touches[0]
    if (!t) return
    // record start coords but don't prevent default yet so the page can scroll
    dragStartX.current = t.clientX
    dragStartY.current = t.clientY
    pendingDragId.current = c.id
    // don't call e.preventDefault() here
  }

  const handleTouchMove = (e: React.TouchEvent, c: TinderCommunity) => {
    const startX = dragStartX.current
    const startY = dragStartY.current
    const t = e.touches && e.touches[0]
    if (startX == null || startY == null || !t) return

    const dx = t.clientX - startX
    const dy = t.clientY - startY

    if (!drag) {
      const absDx = Math.abs(dx)
      const absDy = Math.abs(dy)
      // still within jitter threshold
      if (absDx < START_THRESHOLD && absDy < START_THRESHOLD) return

      if (absDx > absDy && absDx >= START_THRESHOLD && pendingDragId.current === c.id) {
        // start dragging on touch
        pendingDragId.current = null
        setDrag({ id: c.id, x: dx })
        // prevent page scrolling when we decided to drag
        e.preventDefault()
        return
      }

      // vertical scroll dominates -> clear pending and allow default
      if (absDy > absDx) {
        pendingDragId.current = null
        return
      }
    }

    if (!drag || drag.id !== c.id) return

    setDrag({ id: c.id, x: dx })
    e.preventDefault()

    if (!swipe) {
      if (dx > SWIPE_THRESHOLD) {
        setDrag(null)
        dragStartX.current = null
        dragStartY.current = null
        doLike(c)
        return
      }
      if (dx < -SWIPE_THRESHOLD) {
        setDrag(null)
        dragStartX.current = null
        dragStartY.current = null
        doDislike(c)
        return
      }
    }
  }

  const handleTouchEnd = (e: React.TouchEvent, c: TinderCommunity) => {
    // if we never started a drag, just clear pending and allow default behavior
    if (!drag) {
      dragStartX.current = null
      dragStartY.current = null
      pendingDragId.current = null
      return
    }

    const startX = dragStartX.current
    const t = e.changedTouches && e.changedTouches[0]
    if (startX == null || !t) {
      setDrag(null)
      dragStartX.current = null
      dragStartY.current = null
      return
    }
    const dx = t.clientX - startX
    setDrag(null)
    dragStartX.current = null
    dragStartY.current = null

    if (dx > SWIPE_THRESHOLD) {
      doLike(c)
      return
    }
    if (dx < -SWIPE_THRESHOLD) {
      doDislike(c)
      return
    }
  }

  const handleTouchCancel = () => {
    setDrag(null)
    dragStartX.current = null
    dragStartY.current = null
    pendingDragId.current = null
  }

  return (
    <CmschPage loginRequired>
      <title>{config?.app?.siteName || 'CMSch'} | Tinder</title>

      <Box w="100%" mx="auto" px={{ base: 2, md: 4 }}>
        <Box position="relative" mb={6} display="flex" flexDirection={{ base: 'column', md: 'row' }} alignItems="center" gap={4}>
          <Heading as="h1" variant="main-title" textAlign="center" flex={{ base: 'none', md: 1 }}>
            Kör tinder
          </Heading>
          <Button
            as={Link}
            to={`${AbsolutePaths.TINDER}/liked`}
            size={{ base: 'md', md: 'lg' }}
            aria-label="Tinder-matches-button"
            width={{ base: 'full', md: 'auto' }}
          >
            Kedvelt körök
          </Button>
        </Box>
        {displayed.length === 0 ? (
          <Box px={4} py={8} textAlign="center">
            <Text>Minden kört megtekintettél már. A kedvelt köröket megtekintheted összegyűjtve a fenti gombra kattintva.</Text>
          </Box>
        ) : (
          <div style={stackContainerStyle} aria-live="polite">
            {displayed.map((c, idx) => {
              const base = cardWrapperBase(idx, displayed.length)
              const isTop = idx === 0
              const isSwiping = swipe?.id === c.id

              let style: React.CSSProperties = {
                ...base,
                transition: 'transform 320ms ease, opacity 320ms ease'
              }

              if (isSwiping && swipe) {
                const dir = swipe.dir === 'right' ? 1 : -1
                style = {
                  ...style,
                  transform: `translateX(${dir * 480}px) rotate(${dir * 18}deg)`,
                  opacity: 0
                }
              }

              if (drag?.id === c.id && !isSwiping) {
                const dx = drag.x
                const rot = dx * 0.06
                const opacity = Math.max(0.25, 1 - Math.abs(dx) / 800)
                style = {
                  ...style,
                  transform: `translateX(${dx}px) rotate(${rot}deg)`,
                  opacity
                }
              }

              // explicitly type handlers so the analyzer recognizes these props
              let handlers: React.DOMAttributes<HTMLDivElement> = {}
              if (isTop) {
                handlers = {
                  onPointerDown: (e: React.PointerEvent) => handlePointerDown(e, c),
                  onPointerMove: (e: React.PointerEvent) => handlePointerMove(e, c),
                  onPointerUp: (e: React.PointerEvent) => handlePointerUp(e, c),
                  onPointerCancel: handlePointerCancel,
                  // touch fallbacks for mobile
                  onTouchStart: (e: React.TouchEvent) => handleTouchStart(e, c),
                  onTouchMove: (e: React.TouchEvent) => handleTouchMove(e, c),
                  onTouchEnd: (e: React.TouchEvent) => handleTouchEnd(e, c),
                  onTouchCancel: handleTouchCancel
                }
              }

              if (!isTop) {
                style.pointerEvents = 'none'
              } else {
                // Allow vertical touch scrolling while still enabling horizontal drag detection
                style.touchAction = 'pan-y'
              }

              return (
                <div key={c.id} style={style} {...handlers}>
                  <TinderCard data={c} depth={idx} onLike={() => doLike(c)} onDislike={() => doDislike(c)} />
                </div>
              )
            })}
          </div>
        )}
      </Box>
    </CmschPage>
  )
}

export default TinderPage
