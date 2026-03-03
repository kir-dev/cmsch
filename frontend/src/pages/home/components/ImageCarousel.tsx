import { Button } from '@/components/ui/button'
import { cn } from '@/lib/utils.ts'
import { ChevronLeft, ChevronRight } from 'lucide-react'
import { useState } from 'react'

type ImageCarouselProps = {
  images: string[]
}

export const ImageCarousel = ({ images }: ImageCarouselProps) => {
  const [currentImageIndex, setCurrentImageIndex] = useState<number>(0)

  const previousImage = () => {
    const previousIndex = currentImageIndex - 1
    setCurrentImageIndex(previousIndex < 0 ? images.length - 1 : previousIndex)
  }
  const nextImage = () => {
    const nextIndex = currentImageIndex + 1
    setCurrentImageIndex(nextIndex > images.length - 1 ? 0 : nextIndex)
  }
  if (images.length === 0) return null

  return (
    <div className="flex flex-col mt-10 overflow-hidden">
      <div className="relative overflow-hidden">
        <div
          className="flex transition-transform duration-500 ease-in-out"
          style={{
            width: `${images.length * 100}%`,
            transform: `translateX(-${(currentImageIndex / images.length) * 100}%)`
          }}
        >
          {images.map((image) => (
            <div className="flex-1 flex items-center justify-center" key={image}>
              <img src={image} className="w-full max-h-[50rem] object-contain object-center" alt="" />
            </div>
          ))}
        </div>
      </div>
      <div className="flex pt-5 items-center justify-between">
        <DirectionButton direction={Directions.LEFT} onClick={previousImage} />
        <div className="flex items-center space-x-2">
          {images.map((_image, index) => (
            <CurrentImageIndicatorDot key={index} index={index} currentIndex={currentImageIndex} onClick={setCurrentImageIndex} />
          ))}
        </div>
        <DirectionButton direction={Directions.RIGHT} onClick={nextImage} />
      </div>
    </div>
  )
}

type CurrentImageIndicatorDotProps = {
  index: number
  currentIndex: number
  onClick: (index: number) => void
}

const CurrentImageIndicatorDot = ({ index, currentIndex, onClick }: CurrentImageIndicatorDotProps) => {
  return (
    <button
      className={cn(
        'h-[10px] w-[10px] p-0 border-2 border-solid rounded-full cursor-pointer transition-all hover:border-[5px] border-primary',
        index === currentIndex ? 'bg-primary' : 'bg-transparent'
      )}
      onClick={() => {
        onClick(index)
      }}
    />
  )
}

type DirectionButtonProps = {
  direction: Directions
  onClick: () => void
}

const Directions = {
  LEFT: 'left',
  RIGHT: 'right'
}
type Directions = (typeof Directions)[keyof typeof Directions]

const DirectionButton = ({ direction, onClick }: DirectionButtonProps) => {
  return (
    <Button
      variant="ghost"
      size="icon"
      onClick={onClick}
      className="p-0 text-primary"
      aria-label={direction === Directions.LEFT ? 'Előző kép' : 'következő kép'}
    >
      {direction === Directions.LEFT ? <ChevronLeft /> : <ChevronRight />}
    </Button>
  )
}
