import type { FC } from 'react'

type EmbeddedVideoProps = {
  id: string | undefined
}

export const EmbeddedVideo: FC<EmbeddedVideoProps> = ({ id }) => (
  <div className="mt-5 w-full pt-[56.25%] relative">
    <iframe
      src={`https://www.youtube.com/embed/${id}`}
      title="YouTube video player"
      className="absolute inset-0 w-full h-full border-none"
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen
    />
  </div>
)
