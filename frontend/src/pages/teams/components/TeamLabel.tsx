import { Box, theme as chakraTheme } from "@chakra-ui/react";

type ChakraColor = keyof typeof chakraTheme.colors;

type TeamLabelProps = {
  text: string
  color?: ChakraColor
}

const TeamLabel = ({text, color = "teal"}: TeamLabelProps) => {
  return (
    <Box
      border="2px solid"
      bg={`${color}.200`}
      borderColor={`${color}.600`}
      color={`${color}.600`}
      p={1}
      borderRadius="xl"
      fontWeight="bold"
    >
      {text}
    </Box>
  );
};

export default TeamLabel;
