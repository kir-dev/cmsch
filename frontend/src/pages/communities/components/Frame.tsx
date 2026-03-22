import type { FC } from 'react'

type FrameProps = {
  id: string
}

export const Frame: FC<FrameProps> = ({ id }) => (
  <div className="mt-5 w-full pt-[56.25%] relative">
    <iframe
      src={`https://www.youtube.com/embed/${id}`}
      title="Körös tartalom"
      className="absolute inset-0 w-full h-full border-none"
      allowFullScreen
    />
  </div>
)
