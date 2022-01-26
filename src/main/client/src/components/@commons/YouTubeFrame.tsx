import React from 'react'
import { Box } from '@chakra-ui/react'

type YouTubeFrameProps = {
  videoId: string
}

export const YouTubeFrame: React.FC<YouTubeFrameProps> = ({ videoId }) => {
  return (
    <Box mt={5} w="100%" pt="56.25%" position="relative">
      <iframe
        src={`https://www.youtube.com/embed/${videoId}`}
        title="Körös videó"
        frameBorder="0"
        allow="accelerometer; encrypted-media; gyroscope; picture-in-picture"
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
}
