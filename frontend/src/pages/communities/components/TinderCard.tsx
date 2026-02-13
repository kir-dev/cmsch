import React from 'react'
import type { TinderCommunity } from '../../../util/views/tinder'

type Props = {
  data: TinderCommunity
  depth?: number
  onLike?: (c: TinderCommunity) => void
  onDislike?: (c: TinderCommunity) => void
  className?: string
}

export const TinderCard = ({ data, depth = 0, onLike, onDislike, className }: Props) => {
  // Prefer the typed `TinderCommunity` fields and use optional chaining/fallbacks.
  const title = data?.name || 'Unknown community'
  const description = data?.shortDescription || ''
  const image = data?.logo || ''

  // keep depth available (used as data attribute) but do not visually offset cards
  const translateY = 0
  const scale = 1

  const containerStyle: React.CSSProperties = {
    width: 320,
    height: 460,
    borderRadius: 12,
    boxShadow: '0 8px 24px rgba(0,0,0,0.15)',
    overflow: 'hidden',
    background: '#fff',
    display: 'flex',
    flexDirection: 'column',
    userSelect: 'none',
    transform: `translateY(${translateY}px) scale(${scale})`,
    transition: 'transform 160ms ease'
  }

  const imageStyle: React.CSSProperties = {
    height: 300,
    backgroundSize: 'cover',
    backgroundPosition: 'center',
    backgroundImage: image ? `url(${image})` : undefined,
    backgroundColor: '#efefef'
  }

  const bodyStyle: React.CSSProperties = {
    padding: 12,
    display: 'flex',
    flexDirection: 'column',
    gap: 8,
    flex: 1
  }

  const buttonsRow: React.CSSProperties = {
    display: 'flex',
    justifyContent: 'space-between',
    gap: 8,
    marginTop: 'auto'
  }

  return (
    <div className={className} style={containerStyle} role="article" aria-label={title} data-depth={depth}>
      <div style={imageStyle} />
      <div style={bodyStyle}>
        <div style={{ fontWeight: 700, fontSize: 18 }}>{title}</div>
        <div style={{ color: '#555', fontSize: 14, lineHeight: 1.3, maxHeight: 72, overflow: 'hidden' }}>
          {description || <span style={{ color: '#999' }}>No description</span>}
        </div>

        <div style={buttonsRow}>
          <button
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            onClick={() => onDislike && onDislike(data)}
            style={{
              flex: 1,
              padding: '8px 12px',
              background: '#fff',
              border: '1px solid #ddd',
              borderRadius: 8,
              cursor: 'pointer'
            }}
            aria-label="Dislike"
          >
            Dislike
          </button>

          <button
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            onClick={() => onLike && onLike(data)}
            style={{
              flex: 1,
              padding: '8px 12px',
              background: '#06b6d4',
              color: '#fff',
              border: 'none',
              borderRadius: 8,
              cursor: 'pointer'
            }}
            aria-label="Like"
          >
            Like
          </button>
        </div>
      </div>
    </div>
  )
}
