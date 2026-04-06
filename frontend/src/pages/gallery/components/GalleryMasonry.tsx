import { Dialog, DialogContent } from '@/components/ui/dialog'
import { l } from '@/util/language.ts'
import type { GalleryItemView } from '@/util/views/gallery.view.ts'
import { ImageIcon } from 'lucide-react'
import { useState } from 'react'
import GalleryImage from './GalleryImage'

interface GalleryMasonryProps {
  photos: GalleryItemView[]
}

const GalleryMasonry = ({ photos }: GalleryMasonryProps) => {
  const [selectedPhoto, setSelectedPhoto] = useState<GalleryItemView | null>(null)

  if (photos.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-center">
        <ImageIcon className="h-16 w-16 text-muted-foreground mb-4" />
        <p className="text-muted-foreground">{l('gallery-empty-message')}</p>
      </div>
    )
  }

  return (
    <>
      <div className="columns-1 gap-4 space-y-4 sm:columns-2 md:columns-3 lg:columns-4">
        {photos.map((photo) => (
          <div
            key={photo.thumbnailUrl || photo.url}
            className={
              'break-inside-avoid cursor-pointer overflow-hidden ' +
              'rounded-lg shadow-md transition-all duration-300 hover:scale-[1.02] hover:shadow-xl'
            }
            onClick={() => setSelectedPhoto(photo)}
          >
            <GalleryImage src={photo.thumbnailUrl || photo.url} alt={photo.title} />
            {(photo.title || photo.description) && (
              <div className="bg-card p-3">
                {photo.title && <h3 className="text-sm font-bold">{photo.title}</h3>}
                {photo.description && <p className="mt-1 text-xs text-muted-foreground line-clamp-2">{photo.description}</p>}
              </div>
            )}
          </div>
        ))}
      </div>

      <Dialog open={!!selectedPhoto} onOpenChange={(open) => !open && setSelectedPhoto(null)}>
        <DialogContent className="max-w-(--spacing-3xl) p-0 overflow-hidden border-none bg-transparent shadow-none">
          {selectedPhoto && (
            <div className="relative flex flex-col items-center">
              <img src={selectedPhoto.url} alt={selectedPhoto.title} className="max-h-[85vh] w-auto object-contain rounded-lg shadow-2xl" />
            </div>
          )}
        </DialogContent>
      </Dialog>
    </>
  )
}

export default GalleryMasonry
