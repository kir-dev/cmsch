import { Box, Heading, HStack, IconButton, Image, Text, useColorModeValue, VStack } from '@chakra-ui/react'
import { FaHeart, FaTimes } from 'react-icons/fa'

import type { TinderCommunity } from '../../../util/views/tinder'

type Props = {
  data: TinderCommunity
  depth?: number
  onLike?: (c: TinderCommunity) => void
  onDislike?: (c: TinderCommunity) => void
  className?: string
}

export const TinderCard = ({ data, depth = 0, onLike, onDislike, className }: Props) => {
  // Prefer the typed `TinderCommunity` fields and use optional chaining/fallbacks.
  const title = data?.name || 'Unknown community'
  const description = data?.shortDescription || ''
  const image = data?.logo || ''
  const tags = data?.tinderAnswers || []
  const tagColor = useColorModeValue('cyan.100', 'cyan.700')

  const bg = useColorModeValue('white', 'gray.800')
  const infoColor = useColorModeValue('gray.600', 'gray.300')
  const placeholderBg = useColorModeValue('#efefef', '#2A2A2A')

  return (
    <Box
      className={className}
      role="article"
      aria-label={title}
      data-depth={depth}
      bg={bg}
      w={{ base: 'calc(100vw - 2rem)', sm: '380px', md: '416px' }}
      maxW="416px"
      h={{ base: 'min(598px, calc(100vh - 200px))', md: '598px' }}
      borderRadius="16px" /* ~12 * 1.3 */
      boxShadow="lg"
      overflow="hidden"
      sx={{ WebkitOverflowScrolling: 'touch' }}
      userSelect="none"
      transition="transform 160ms ease"
      display="flex"
      flexDirection="column"
    >
      {image ? (
        // Prevent the browser from starting a native drag from the image so card drag gestures work.
        <Image
          src={image}
          alt={title}
          width="100%"
          height="100%"
          marginTop={{ base: 4, md: 10 }}
          objectFit="contain"
          draggable={false}
          onDragStart={(e) => e.preventDefault()}
          sx={{ WebkitUserDrag: 'none' }}
        />
      ) : (
        <Box
          bg={placeholderBg}
          width="100%"
          height="100%"
          // also prevent dragging the placeholder box
          draggable={false}
          onDragStart={(e) => e.preventDefault()}
          sx={{ WebkitUserDrag: 'none' }}
        />
      )}

      {/* VStack becomes a column flex container. The top part scrolls, buttons stay outside the scroll area. */}
      <VStack align="stretch" p={{ base: 2, md: 3 }} flex="1" display="flex" flexDirection="column">
        <HStack justify="space-around" mt={2}>
          <Box flex="1" overflowY="auto">
            <Heading as="h3" size={{ base: 'sm', md: 'md' }}>
              {title}
            </Heading>

            <Text fontSize={{ base: 'xs', md: 'sm' }} color={infoColor} mt={2}>
              Alapítva: {data?.established ?? '—'}
              <br />
              Reszort: {data?.resortName ?? '—'}
            </Text>

            <Box
              mt={2}
              /* let the description area size naturally inside the scrollable box */
              color={useColorModeValue('#555', '#ccc')}
              fontSize={{ base: 'xs', md: 'sm' }}
              lineHeight="1.4"
              aria-hidden={description.length === 0}
            >
              {description ? <Text whiteSpace="normal">{description}</Text> : <Text color="gray.400">No description</Text>}
            </Box>
          </Box>

          <Box>
            {tags.length > 0 && (
              <VStack align="flex-start" spacing={1} mt={2}>
                {tags.slice(0, 5).map((tag, index) => (
                  <Box key={index} bg={tagColor} px={2} py={1} borderRadius="md" fontSize={{ base: '2xs', md: 'xs' }}>
                    {tag}
                  </Box>
                ))}
              </VStack>
            )}
          </Box>
        </HStack>

        {/* Buttons stay outside the scrollable Box so they're always visible */}
        <HStack justify="space-around" mt={2}>
          <IconButton
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            aria-label="Dislike"
            title="Dislike"
            icon={<FaTimes />}
            onClick={() => onDislike && onDislike(data)}
            variant="outline"
            colorScheme="red"
            borderRadius="full"
            w={{ base: '48px', md: '56px' }}
            h={{ base: '48px', md: '56px' }}
            minW={{ base: '48px', md: '56px' }}
            minH={{ base: '48px', md: '56px' }}
            p={0}
          />

          <IconButton
            data-no-drag
            onPointerDown={(e) => e.stopPropagation()}
            aria-label="Like"
            title="Like"
            icon={<FaHeart />}
            onClick={() => onLike && onLike(data)}
            bg="cyan.500"
            color="white"
            _hover={{ bg: 'cyan.600' }}
            borderRadius="full"
            w={{ base: '48px', md: '56px' }}
            h={{ base: '48px', md: '56px' }}
            minW={{ base: '48px', md: '56px' }}
            minH={{ base: '48px', md: '56px' }}
            p={0}
          />
        </HStack>
      </VStack>
    </Box>
  )
}
