import { Box } from '@chakra-ui/react'
import { FC } from 'react'

type FrameProps = {
  id: string
}

export const Frame: FC<FrameProps> = ({ id }) => (
  <Box mt={5} w="100%" pt="56.25%" position="relative">
    <iframe
      src={`https://www.youtube.com/embed/${id}`}
      title="Körös tartalom"
      allow=""
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
