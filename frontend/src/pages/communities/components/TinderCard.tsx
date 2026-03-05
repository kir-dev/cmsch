import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils'
import { useColorModeValue } from '@/util/core-functions.util'
import type { TinderCommunity } from '@/util/views/tinder'
import { Heart, X } from 'lucide-react'
import type { CSSProperties } from 'react'

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
  const tags = data?.tinderAnswers || []
  const tagColor = useColorModeValue('bg-info/10 text-info border-info/20', 'dark:bg-info/20 dark:text-info-foreground border-info/30')

  const bg = useColorModeValue('bg-card', 'dark:bg-card')
  const infoColor = useColorModeValue('text-muted-foreground', 'dark:text-muted-foreground')
  const placeholderBg = useColorModeValue('bg-foreground/10', 'dark:bg-foreground/10')

  return (
    <div
      className={cn(
        'flex flex-col w-[calc(100vw-2rem)] sm:w-[380px] md:w-[416px] max-w-[416px] ' +
          'h-[min(598px,calc(100vh-200px))] md:h-[598px] rounded-2xl shadow-lg ' +
          'overflow-hidden transition-transform duration-150 ease-in select-none',
        bg,
        className
      )}
      role="article"
      aria-label={title}
      data-depth={depth}
    >
      {image ? (
        // Prevent the browser from starting a native drag from the image so card drag gestures work.
        <img
          src={image}
          alt={title}
          className={cn('w-full h-full mt-4 md:mt-10 object-contain', placeholderBg)}
          draggable={false}
          onDragStart={(e) => e.preventDefault()}
          style={{ WebkitUserDrag: 'none' } as CSSProperties}
        />
      ) : (
        <div
          className={cn('w-full h-full', placeholderBg)}
          // also prevent dragging the placeholder box
          draggable={false}
          onDragStart={(e) => e.preventDefault()}
          style={{ WebkitUserDrag: 'none' } as CSSProperties}
        />
      )}

      <div className="flex flex-col p-2 md:p-3 flex-1 overflow-hidden">
        <div className="flex justify-around mt-2 overflow-hidden gap-2">
          <div className="flex-1 overflow-y-auto min-h-0">
            <h3 className="text-lg md:text-xl font-bold leading-tight">{title}</h3>

            <p className={cn('text-xs md:text-sm mt-2', infoColor)}>
              Alapítva: {data?.established ?? '—'}
              <br />
              Reszort: {data?.resortName ?? '—'}
            </p>

            <div
              className={cn('mt-2 text-xs md:text-sm leading-relaxed', useColorModeValue('text-[#555]', 'text-[#ccc]'))}
              aria-hidden={description.length === 0}
            >
              {description ? <p className="whitespace-normal">{description}</p> : <p className="text-gray-400">No description</p>}
            </div>
          </div>

          <div className="shrink-0">
            {tags.length > 0 && (
              <div className="flex flex-col items-start gap-1 mt-2">
                {tags.slice(0, 5).map((tag, index) => (
                  <div key={index} className={cn('px-2 py-1 rounded-md text-[10px] md:text-xs', tagColor)}>
                    {tag}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>

        <div className="flex justify-around mt-auto py-2">
          <Button
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            aria-label="Dislike"
            title="Dislike"
            onClick={() => onDislike && onDislike(data)}
            variant="outline"
            className="rounded-full w-12 md:w-14 h-12 md:h-14 p-0 text-danger hover:bg-danger/10 border-danger/20"
          >
            <X className="h-6 w-6" />
          </Button>

          <Button
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            aria-label="Like"
            title="Like"
            onClick={() => onLike && onLike(data)}
            className="rounded-full w-12 md:w-14 h-12 md:h-14 p-0 bg-info text-info-foreground hover:bg-info/90"
          >
            <Heart className="h-6 w-6" />
          </Button>
        </div>
      </div>
    </div>
  )
}
