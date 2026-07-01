import { Skeleton } from '@/components/ui/skeleton'
import { cn } from '@/lib/utils'
import { useEffect, useMemo, useState } from 'react'

interface GalleryImageProps {
  src: string
  alt: string
  className?: string
  onClick?: () => void
}

const aspectRatios = ['aspect-[3/4]', 'aspect-[4/3]', 'aspect-[16/9]', 'aspect-[1/1]', 'aspect-[2/3]']
const getRandomAspectRatio = () => aspectRatios[Math.floor(Math.random() * aspectRatios.length)]

const GalleryImage = ({ src, alt, className, onClick }: GalleryImageProps) => {
  const [loaded, setLoaded] = useState(false)
  const aspectRatio = useMemo(() => getRandomAspectRatio(), [])

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLoaded(false)
  }, [src])

  const handleImageRef = (img: HTMLImageElement | null) => {
    if (img && img.complete && img.naturalWidth > 0) {
      setLoaded(true)
    }
  }

  return (
    <div className={className}>
      {!loaded && <Skeleton className={cn('rounded-lg', aspectRatio)} />}
      <img
        ref={handleImageRef}
        src={src}
        alt={alt}
        className={cn('h-auto w-full object-cover', loaded ? 'block' : 'hidden')}
        onLoad={() => setLoaded(true)}
        onClick={onClick}
      />
    </div>
  )
}

export default GalleryImage
