import React from 'react'
import { Box } from '@chakra-ui/react'

type FrameProps = {
  id: string
}

export const Frame: React.FC<FrameProps> = ({ id }) => {
  return (
    <Box mt={5} w="100%" pt="56.25%" position="relative">
      <iframe
        src={`https://drive.google.com/file/d/${id}/preview`}
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
}
