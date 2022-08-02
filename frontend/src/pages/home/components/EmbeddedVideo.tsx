import { Box } from '@chakra-ui/react'
import { FC } from 'react'

type EmbeddedVideoProps = {
  id: string | undefined
}

export const EmbeddedVideo: FC<EmbeddedVideoProps> = ({ id }) => (
  <Box mt={5} w="100%" pt="56.25%" position="relative">
    <iframe
      src={`https://www.youtube.com/embed/${id}`}
      title=""
      frameBorder={0}
      allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
      allowFullScreen
      style={{
        position: 'absolute',
        top: 0,
        left: 0,
        bottom: 0,
        right: 0,
        width: '100%',
        height: '100%'
      }}
    />
  </Box>
)
