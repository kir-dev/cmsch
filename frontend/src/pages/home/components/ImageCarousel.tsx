import { Carousel, type CarouselApi, CarouselContent, CarouselItem, CarouselNext, CarouselPrevious } from '@/components/ui/carousel'
import { cn } from '@/lib/utils.ts'
import { useCallback, useState } from 'react'

type ImageCarouselProps = {
  images: string[]
}

export const ImageCarousel = ({ images }: ImageCarouselProps) => {
  const [api, setApi] = useState<CarouselApi>()
  const [current, setCurrent] = useState(0)
  const [count, setCount] = useState(0)

  const onSetApi = useCallback((carouselApi: CarouselApi) => {
    setApi(carouselApi)
    if (!carouselApi) return

    setCount(carouselApi.scrollSnapList().length)
    setCurrent(carouselApi.selectedScrollSnap())

    carouselApi.on('select', () => {
      setCurrent(carouselApi.selectedScrollSnap())
    })
  }, [])

  if (images.length === 0) return null

  return (
    <div className="flex flex-col mt-10">
      <Carousel setApi={onSetApi} opts={{ loop: true }} className="w-full">
        <CarouselContent>
          {images.map((image) => (
            <CarouselItem key={image}>
              <div className="flex items-center justify-center">
                <img src={image} className="w-full max-h-200 object-contain object-center" alt="" />
              </div>
            </CarouselItem>
          ))}
        </CarouselContent>
        <CarouselPrevious className="left-2" />
        <CarouselNext className="right-2" />
      </Carousel>
      {count > 1 && (
        <div className="flex items-center justify-center space-x-2 pt-4">
          {Array.from({ length: count }).map((_, index) => (
            <button
              key={index}
              type="button"
              aria-label={`Ugrás a(z) ${index + 1}. képre`}
              aria-current={index === current}
              className={cn(
                'h-[10px] w-[10px] p-0 border-2 border-solid rounded-full cursor-pointer transition-all hover:border-[5px] border-primary',
                index === current ? 'bg-primary' : 'bg-transparent'
              )}
              onClick={() => api?.scrollTo(index)}
            />
          ))}
        </div>
      )}
    </div>
  )
}
