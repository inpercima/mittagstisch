module.exports = {
  extends: ["@commitlint/config-conventional"],
  rules: {
    "type-enum": [2, "always", ["feat", "fix", "chore", "refactor", "build", "ci", "docs", "test", "perf", "revert"]],
  },
};
