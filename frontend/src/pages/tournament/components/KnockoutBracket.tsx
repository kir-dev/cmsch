import { MatchTree } from '../util/matchTree.ts'
import { Box, Stack } from '@chakra-ui/react'
import Match from './Match.tsx'

interface KnockoutBracketProps {
  tree: MatchTree
}

const KnockoutBracket: React.FC<KnockoutBracketProps> = ({ tree }: KnockoutBracketProps) => {
  return (
    <Stack direction="row" position="relative">
      {(tree.upperTree || tree.lowerTree) && (
        <>
          <Stack direction="column" align="center" mr={-2} mb={2}>
            {tree.upperTree && <KnockoutBracket tree={tree.upperTree} />}
            {tree.lowerTree && <KnockoutBracket tree={tree.lowerTree} />}
          </Stack>
          <Box width="40" mr={-2}>
            {tree.upperTree && tree.lowerTree && (
              <>
                <Box top="25%" left="0" position="relative" width="50%" height="2px" bg="gray.300" />
                <Box top="75%" left="0" position="relative" width="50%" height="2px" bg="gray.300" />
                <Box top="50%" left="50%" position="relative" width="50%" height="2px" bg="gray.300" />
                <Box top="22.5%" left="50%" position="relative" width="2px" height="51.5%" bg="gray.300" />
              </>
            )}
            {tree.upperTree && !tree.lowerTree && <Box top="50%" left="0" position="relative" width="100%" height="2px" bg="gray.300" />}
            {!tree.upperTree && tree.lowerTree && <Box top="50%" left="0" position="relative" width="100%" height="2px" bg="gray.300" />}
          </Box>
        </>
      )}
      <Box alignContent="center">
        <Match match={tree.root} />
      </Box>
    </Stack>
  )
}

export default KnockoutBracket
